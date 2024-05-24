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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoField;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final MqttService mqttService;
    private final ObjectMapper objectMapper;
    private final RedisAdapter redisAdapter;
    private final CommonAdapter commonAdapter;
    private final SensorService sensorService;
    private final DeviceService deviceService;
    private final MessageService messageService;
    private final OccupancyService occupancyService;
    private final ApplicationContext applicationContext;
    private final Map<String, String> latestValues = new HashMap<>();


    public void createRule(RuleInfo ruleInfo) throws JsonProcessingException {
        String place = ruleInfo.getPlace();
        String deviceName = ruleInfo.getDeviceName();
        List<Object> beans = new ArrayList<>();

        if (!redisAdapter.hasKey("rule_infos", String.valueOf(ruleInfo.hashCode()))) {
            redisAdapter.setValueToHash("rule_infos", String.valueOf(ruleInfo.hashCode()), objectMapper.writeValueAsString(ruleInfo));
        }

        createLatestValueSavingFlow(ruleInfo, beans);
        updateOnOffValue(place, deviceName, ruleInfo.getCustomMode());
        if (ruleInfo.getAiMode().isPresent()) {
            createAiModeScheduledFlow(place, deviceName, ruleInfo.getAiMode().get(), beans);
        }
        createCustomModeFlow(place, deviceName, ruleInfo.getCustomMode(), beans);

        registerBean(place + "." + deviceName + ".", beans);
    }

    private void createLatestValueSavingFlow(RuleInfo ruleInfo, List<Object> beans) {
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

                                                       return payload;
                                                   }).nullChannel();
        beans.add(flow);
    }

    private void createAiModeScheduledFlow(String place, String deviceName, AiMode aiMode, List<Object> beans) {
        IntegrationFlow flow = IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                                     c -> c.poller(Pollers.fixedRate(aiMode.getTimeInterval().getLong(ChronoField.MILLI_OF_SECOND))))
                                               .filter(p -> deviceService.isAiMode(place, deviceName) && !deviceService.isCustomMode(place, deviceName))
                                               .handle((payload, headers) ->
                                                           {
                                                               Map<String, Object> messageValue = new HashMap<>();
                                                               messageValue.put("time", System.currentTimeMillis());
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

                                                               return null;
                                                           }).nullChannel();
        beans.add(flow);
    }

    private void createCustomModeFlow(String place, String deviceName, CustomMode customMode, List<Object> beans) {
        IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                                                   c -> c.poller(Pollers.fixedRate(customMode.getTimeInterval().getLong(ChronoField.MILLI_OF_SECOND))))
                                                             .filter(p -> !deviceService.isAiMode(place, deviceName) && deviceService.isCustomMode(place, deviceName));

        if (customMode.isOccupancyCheckRequired()) {
            flowBuilder = flowBuilder.filter(Payload.class, payload -> Constants.OCCUPIED.equals(occupancyService.getOccupancyStatus(place)),
                                             e -> e.discardFlow(flow -> flow.handle((payload, headers) ->
                                                                                        {
                                                                                            if (deviceService.isDevicePowered(deviceName)) {
                                                                                                messageService.sendDeviceMessage(new ValueMessage(place, deviceName, false));
                                                                                            }
                                                                                            return null;
                                                                                        }).nullChannel()));
        }

        IntegrationFlow flow = flowBuilder.handle((payload, headers) ->
                                                      {
                                                          if (isAllOnConditionsTrue(customMode) && !deviceService.isDevicePowered(deviceName)) {
                                                              messageService.sendDeviceMessage(new ValueMessage(place, deviceName, true));
                                                              return payload;
                                                          }
                                                          if (isAllOffConditionsTrue(customMode) && deviceService.isDevicePowered(deviceName)) {
                                                              messageService.sendDeviceMessage(new ValueMessage(place, deviceName, false));
                                                          }

                                                          return null;
                                                      }).nullChannel();
        beans.add(flow);
    }


    private MessageChannel createMessageChannel(List<Object> beans) {
        MessageChannel customChannel = new DirectChannel();
        beans.add(customChannel);
        return customChannel;
    }

    private void createMqttAdapters(MessageChannel channel, Collection<MqttInInfo> mqttInInfoSet, List<Object> beans) {
        for (MqttInInfo mqttInInfo : mqttInInfoSet) {
            MqttPahoMessageDrivenChannelAdapter adapter = mqttService.createMqttAdapter(mqttInInfo.getMqttUrl(),
                                                                                        UUID.randomUUID().toString(), channel, mqttInInfo.getTopic());
            beans.add(adapter);
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
            commonAdapter.updateSensorByDeviceAndSensor(build);
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

    private <T> void registerBean(String prefix, List<T> list) {
        for (T object : list) {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition((Class<T>) object.getClass(), () -> object).getBeanDefinition();
            ((BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory()).registerBeanDefinition(
                    prefix + beanDefinition.getBeanClass().getSimpleName(), beanDefinition);

        }
    }

    private Set<MqttInInfo> combineUniqueMqttInfos(Collection<MqttInInfo> mqttInfos, Collection<MqttInInfo> mqttInfos2) {
        HashSet<MqttInInfo> set = new HashSet<>(mqttInfos);
        set.addAll(mqttInfos2);
        return set;
    }
}
