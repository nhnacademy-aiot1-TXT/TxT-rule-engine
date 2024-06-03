package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorRequest;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.dto.rule.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@IntegrationComponentScan
@RequiredArgsConstructor
public class RuleService {
    private final MqttService mqttService;
    private final ObjectMapper objectMapper;
    private final RedisAdapter redisAdapter;
    private final CommonAdapter commonAdapter;
    private final SensorService sensorService;
    private final DeviceService deviceService;
    @Autowired
    private IntegrationFlowContext flowContext;
    private final MessageService messageService;
    private final OccupancyService occupancyService;
    private final ApplicationContext applicationContext;
    private final Map<String, String> latestValues = new HashMap<>();

    public void updateRule(RuleInfo ruleInfo) throws JsonProcessingException {
        String place = ruleInfo.getPlace();
        String deviceName = ruleInfo.getDeviceName();

        if (redisAdapter.hasKey("rule_infos", place + "_" + deviceName)) {
            deleteBeans(place + "." + deviceName + ".");
            commonAdapter.deleteSensorsByPlaceAndDevice(place, deviceName);
        }

        createRule(ruleInfo);
        redisAdapter.setValueToHash("rule_infos", place + "_" + deviceName, objectMapper.writeValueAsString(ruleInfo));
    }

    public void createRule(RuleInfo ruleInfo) {
        String place = ruleInfo.getPlace();
        String deviceName = ruleInfo.getDeviceName();
        Map<String, Object> beans = new HashMap<>();
        Map<String, IntegrationFlow> flows = new HashMap<>();

        createLatestValueSavingFlow(ruleInfo, beans, flows);
        updateOnOffValue(place, deviceName, ruleInfo.getCustomMode());
        if (ruleInfo.getAiMode().isPresent()) {
            createAiModeScheduledFlow(place, deviceName, ruleInfo.getAiMode().get(), flows);
        }
        createCustomModeFlow(place, deviceName, ruleInfo.getCustomMode(), flows);

        registerFlows(place + "." + deviceName + ".", flows);
        registerBean(place + "." + deviceName + ".", beans);
    }

    private void createLatestValueSavingFlow(RuleInfo ruleInfo, Map<String, Object> beans, Map<String, IntegrationFlow> flows) {
        MessageChannel messageChannel = createMessageChannel(beans);
        Set<MqttInInfo> customMqttInInfos = ruleInfo.getCustomMode().getMqttConditionMap().keySet();
        Set<MqttInInfo> mqttInInfos = customMqttInInfos;
        if (ruleInfo.getAiMode().isPresent()) {
            mqttInInfos = combineUniqueMqttInfos(ruleInfo.getAiMode().get().getMqttInInfos(), customMqttInInfos);
        }
        createMqttAdapters(messageChannel, mqttInInfos, beans);

        IntegrationFlow flow = IntegrationFlows.from(messageChannel)
                                               .transform(sensorService::convertStringToPayload)
                                               .handle(Payload.class, (payload, headers) ->
                                                   {
                                                       String[] topics = sensorService.getTopics(headers);
                                                       String sensorPlace = sensorService.getPlace(topics);
                                                       String sensorMeasurement = sensorService.getMeasurement(topics);

                                                       latestValues.put(sensorPlace + "_" + sensorMeasurement, payload.getValue());
                                                       latestValues.put("time", String.valueOf(payload.getTime()));
                                                       log.info("latestValue: " + sensorPlace + "_" + sensorMeasurement + ":" + payload.getValue());

                                                       return payload;
                                                   }).nullChannel();
        flows.put(flow.getClass().getSimpleName() + ".latestValueFlow", flow);
    }

    private void createAiModeScheduledFlow(String place, String deviceName, AiMode aiMode, Map<String, IntegrationFlow> flows) {
        IntegrationFlow flow = IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                                     c -> c.poller(Pollers.fixedRate(aiMode.getTimeInterval().toNanoOfDay() / 1_000_000)))
                                               .filter(p -> deviceService.isAiMode(place, deviceName) && !deviceService.isCustomMode(place, deviceName))
                                               .handle((payload, headers) ->
                                                           {
                                                               log.info(place + "." + deviceName + ".aiModeFlow 실행");
                                                               return payload;
                                                           })
                                               .handle((payload, headers) ->
                                                           {
                                                               Map<String, Object> messageValue = new HashMap<>();
                                                               //                                                               messageValue.put("time", System.currentTimeMillis());
                                                               messageValue.put("place", place);
                                                               messageValue.put("deviceName", deviceName);

                                                               for (MqttInInfo mqttInInfo : aiMode.getMqttInInfos()) {
                                                                   String sensorPlace = mqttInInfo.getPlace();
                                                                   String sensorMeasurement = mqttInInfo.getMeasurement();

                                                                   if (getLatestValue(sensorPlace, sensorMeasurement).isEmpty()) {
                                                                       return null;
                                                                   }

                                                                   messageValue.put(sensorPlace + "_" + sensorMeasurement, getLatestValue(sensorPlace, sensorMeasurement).get());
                                                               }

                                                               messageService.sendPredictMessage(messageValue);
                                                               log.info(place + "_" + deviceName + ": send Predict Message");

                                                               return null;
                                                           }).nullChannel();
        flows.put(flow.getClass().getSimpleName() + ".aiModeFlow", flow);
    }

    private void createCustomModeFlow(String place, String deviceName, CustomMode customMode, Map<String, IntegrationFlow> flows) {
        IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                                                   c -> c.poller(Pollers.fixedRate(customMode.getTimeInterval().toNanoOfDay() / 1_000_000)))
                                                             .filter(p -> deviceService.isCustomMode(place, deviceName))
                                                             .handle((payload, headers) ->
                                                                         {
                                                                             log.info(place + "." + deviceName + ".customModeFlow 실행");
                                                                             return payload;
                                                                         });

        if (customMode.isOccupancyCheckRequired()) {
            flowBuilder = flowBuilder.filter(payload -> Constants.OCCUPIED.equals(occupancyService.getOccupancyStatus(place)),
                                             e -> e.discardFlow(flow -> flow.handle((payload, headers) ->
                                                                                        {
                                                                                            if (deviceService.isDevicePowered(place, deviceName)) {
                                                                                                messageService.sendDeviceMessage(new ValueMessage(place, deviceName, false));
                                                                                                log.info(place + "_" + deviceName + ": send off Message");
                                                                                            }
                                                                                            return null;
                                                                                        }).nullChannel()));
        } else {
            flowBuilder = flowBuilder.handle((payload, headers) ->
                                                 {
                                                     if (Constants.VACANT.equals(occupancyService.getOccupancyStatus(place)) && deviceService.isDevicePowered(place, deviceName)) {
                                                         messageService.sendDeviceMessage(new ValueMessage(place, deviceName, false));
                                                         log.info(place + "_" + deviceName + ": send off Message");
                                                     }
                                                     return payload;
                                                 });
        }

        IntegrationFlow flow = flowBuilder.handle((payload, headers) ->
                                                      {
                                                          if (isAllOnConditionsTrue(customMode) && !deviceService.isDevicePowered(place, deviceName)) {
                                                              messageService.sendDeviceMessage(new ValueMessage(place, deviceName, true));
                                                              log.info(place + "_" + deviceName + ": send on Message");
                                                              return payload;
                                                          }
                                                          if (isAllOffConditionsTrue(customMode) && deviceService.isDevicePowered(place, deviceName)) {
                                                              messageService.sendDeviceMessage(new ValueMessage(place, deviceName, false));
                                                              log.info(place + "_" + deviceName + ": send off Message");
                                                          }

                                                          return null;
                                                      }).nullChannel();
        flows.put(flow.getClass().getSimpleName() + ".customModeFlow", flow);
    }


    private MessageChannel createMessageChannel(Map<String, Object> beans) {
        MessageChannel customChannel = new DirectChannel();
        beans.put(customChannel.getClass().getSimpleName(), customChannel);
        return customChannel;
    }

    private void createMqttAdapters(MessageChannel channel, Collection<MqttInInfo> mqttInInfoSet, Map<String, Object> beans) {
        int i = 1;
        for (MqttInInfo mqttInInfo : mqttInInfoSet) {
            MqttPahoMessageDrivenChannelAdapter adapter = mqttService.createMqttAdapter(mqttInInfo.getMqttUrl(),
                                                                                        UUID.randomUUID().toString(), channel, mqttInInfo.getTopic());
            adapter.start();
            log.info("MQTT adapter 생성 완료: " + mqttInInfo.getMqttUrl() + " : " + mqttInInfo.getTopic());
            beans.put(adapter.getClass().getSimpleName() + "#" + i++, adapter);
        }
    }

    private void updateOnOffValue(String place, String deviceName, CustomMode customMode) {
        Set<Map.Entry<MqttInInfo, ConditionSet>> entries = customMode.getMqttConditionMap().entrySet();
        for (Map.Entry<MqttInInfo, ConditionSet> entry : entries) {
            String measurement = sensorService.getMeasurement(entry.getKey().getTopic().split("/"));
            DeviceSensorRequest build = DeviceSensorRequest.builder()
                                                           .deviceName(deviceName).sensorName(measurement).placeName(place)
                                                           .onValue(entry.getValue().getOnCondition().getStandardValue())
                                                           .offValue(entry.getValue().getOffCondition().isPresent() ? entry.getValue().getOffCondition().get().getStandardValue() : -1)
                                                           .build();
            try {
                commonAdapter.updateSensorByDeviceAndSensor(build);
            } catch (FeignException.NotFound e) {
                commonAdapter.addSensor(build);
            }
        }
    }

    private boolean isAllOnConditionsTrue(CustomMode customMode) {
        for (Map.Entry<MqttInInfo, ConditionSet> entry : customMode.getMqttConditionMap().entrySet()) {
            MqttInInfo mqttInInfo = entry.getKey();
            CompareCondition onCondition = entry.getValue().getOnCondition();

            if (getLatestValue(mqttInInfo.getPlace(), mqttInInfo.getMeasurement()).isEmpty() ||
                    !onCondition.test(sensorService.parseToFloatValue(getLatestValue(mqttInInfo.getPlace(), mqttInInfo.getMeasurement()).get()))) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllOffConditionsTrue(CustomMode customMode) {
        for (Map.Entry<MqttInInfo, ConditionSet> entry : customMode.getMqttConditionMap().entrySet()) {
            MqttInInfo mqttInInfo = entry.getKey();

            if (entry.getValue().getOffCondition().isEmpty()) {
                continue;
            }

            CompareCondition offCondition = entry.getValue().getOffCondition().get();
            if (getLatestValue(mqttInInfo.getPlace(), mqttInInfo.getMeasurement()).isEmpty() ||
                    !offCondition.test(sensorService.parseToFloatValue(getLatestValue(mqttInInfo.getPlace(), mqttInInfo.getMeasurement()).get()))) {
                return false;
            }
        }
        return true;
    }

    private Optional<String> getLatestValue(String place, String measurement) {
        return Optional.ofNullable(latestValues.get(place + "_" + measurement));
    }

    private <T> void registerBean(String prefix, Map<String, T> map) {
        for (Map.Entry<String, T> entry : map.entrySet()) {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition((Class<T>) entry.getValue().getClass(), () -> entry.getValue()).getBeanDefinition();
            ((BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory()).registerBeanDefinition(prefix + entry.getKey(), beanDefinition);
        }

        log.info(prefix + "bean 생성 완료");
    }

    private void deleteBeans(String prefix) {
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        for (String name : beanFactory.getBeanDefinitionNames()) {
            if (name.startsWith(prefix) && flowContext.getRegistry().containsKey(name)) {
                deleteFlow(name);
            }
        }
        for (String name : beanFactory.getBeanDefinitionNames()) {
            if (name.startsWith(prefix)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                if (((AbstractBeanDefinition) beanDefinition).getBeanClass().equals(MqttPahoMessageDrivenChannelAdapter.class)) {
                    mqttService.stopMqttAdapter(name);
                    log.info(name + " 중지");
                }
                beanFactory.removeBeanDefinition(name);
            }
        }

        log.info(prefix + "로 시작하는 기존 bean 모두 삭제 완료");
    }

    private void registerFlows(String prefix, Map<String, IntegrationFlow> map) {
        for (Map.Entry<String, IntegrationFlow> entry : map.entrySet()) {
            flowContext.registration(entry.getValue()).id(prefix + entry.getKey()).register();
        }
        log.info(prefix + "flow 등록 완료");
    }

    private void deleteFlow(String flowName) {
        flowContext.remove(flowName);
    }

    private Set<MqttInInfo> combineUniqueMqttInfos(Collection<MqttInInfo> mqttInfos, Collection<MqttInInfo> mqttInfos2) {
        HashSet<MqttInInfo> set = new HashSet<>(mqttInfos);
        set.addAll(mqttInfos2);
        return set;
    }
}
