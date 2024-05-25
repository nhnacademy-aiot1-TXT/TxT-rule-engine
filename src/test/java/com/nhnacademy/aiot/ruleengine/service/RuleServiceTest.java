package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorRequest;
import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
import com.nhnacademy.aiot.ruleengine.dto.rule.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@EnableIntegration
@SpringJUnitConfig(classes = {RuleService.class, SensorService.class, RuleServiceTest.TestConfig.class})
class RuleServiceTest {

    @MockBean
    private MqttService mqttService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RedisAdapter redisAdapter;
    @MockBean
    private CommonAdapter commonAdapter;
    @MockBean
    private DeviceService deviceService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private OccupancyService occupancyService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private IntegrationFlowContext flowContext;
    @Autowired
    private RuleService ruleService;
    private Map<String, Object> headersMap;

    @BeforeEach
    void setUp() {
        headersMap = new HashMap<>();
        headersMap.put("mqtt_receivedRetained", false);
        headersMap.put("mqtt_id", 2);
        headersMap.put("mqtt_duplicate", false);
        headersMap.put("mqtt_receivedQos", 2);

        doNothing().when(messageService).sendPredictMessage(anyMap());
        doNothing().when(messageService).sendDeviceMessage(any(ValueMessage.class));
        when(mqttService.createMqttAdapter(anyString(), anyString(), any(MessageChannel.class), anyString())).thenReturn(mock(MqttPahoMessageDrivenChannelAdapter.class));
        when(commonAdapter.updateSensorByDeviceAndSensor(any(DeviceSensorRequest.class))).thenReturn(mock(ResponseEntity.class));
    }

    @Test
    void nonEmptyRuleInfo_aiMode() {
        String place = Constants.CLASS_A;
        String deviceName = Constants.AIRCONDITIONER;
        Message<String> temperatrueMsg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124128c067999/e/temperature", 29);
        Message<String> humidityMsg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124785c421885/e/humidity", 500);
        AiMode aiMode = new AiMode(
                List.of(new MqttInInfo("testUrl", "data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124128c067999/e/temperature"),
                        new MqttInInfo("testUrl2", "data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124785c421885/e/humidity")),
                LocalTime.of(0, 1));
        CustomMode customMode = new CustomMode(true,
                                               Map.of(new MqttInInfo("testUrl", "data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124128c067999/e/temperature"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN_OR_EQUAL, 26f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 18f))),
                                               LocalTime.of(0, 1));
        RuleInfo ruleInfo = new RuleInfo(place, deviceName, aiMode, customMode);
        when(deviceService.isAiMode(place, deviceName)).thenReturn(true);
        when(deviceService.isCustomMode(place, deviceName)).thenReturn(false);
        ArgumentCaptor<Map<String, Object>> captor = forClass(Map.class);

        ruleService.createRule(ruleInfo);
        flowContext.registration(applicationContext.getBean("class_a.airconditioner.StandardIntegrationFlow.latestValueFlow", IntegrationFlow.class)).id("latestValueFlow").register();
        flowContext.registration(applicationContext.getBean("class_a.airconditioner.StandardIntegrationFlow.aiModeFlow", IntegrationFlow.class)).id("aiModeFlow").register();
        flowContext.registration(applicationContext.getBean("class_a.airconditioner.StandardIntegrationFlow.customModeFlow", IntegrationFlow.class)).id("customModeFlow").register();
        MessageChannel channel = applicationContext.getBean("class_a.airconditioner.DirectChannel", MessageChannel.class);
        channel.send(temperatrueMsg);
        channel.send(humidityMsg);

        verify(mqttService).createMqttAdapter(eq("testUrl"), anyString(), any(MessageChannel.class), eq("data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124128c067999/e/temperature"));
        verify(mqttService).createMqttAdapter(eq("testUrl2"), anyString(), any(MessageChannel.class), eq("data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124785c421885/e/humidity"));
        verify(commonAdapter).updateSensorByDeviceAndSensor(any(DeviceSensorRequest.class));
        await().atMost(2, TimeUnit.MINUTES).untilAsserted(() -> verify(messageService).sendPredictMessage(captor.capture()));
        assertEquals("29", captor.getValue().get("class_a_temperature"));
        assertEquals("500", captor.getValue().get("class_a_humidity"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.DirectChannel"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.StandardIntegrationFlow.latestValueFlow"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.StandardIntegrationFlow.aiModeFlow"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.StandardIntegrationFlow.customModeFlow"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.MqttPahoMessageDrivenChannelAdapter#1"));
        assertNotNull(applicationContext.getAutowireCapableBeanFactory().getBean("class_a.airconditioner.MqttPahoMessageDrivenChannelAdapter#2"));

        flowContext.remove("latestValueFlow");
        flowContext.remove("aiModeFlow");
        flowContext.remove("customModeFlow");
    }

    @Test
    void aiModeEmptyRuleInfo_customMode_on() {
        String place = "class_b";
        String deviceName = Constants.AIRCLEANER;
        Message<String> tvoc500Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc", 500);
        Message<String> temp29Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature", 29);
        CustomMode customMode = new CustomMode(true,
                                               Map.of(new MqttInInfo("testUrl", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN_OR_EQUAL, 500f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 200f)),
                                                      new MqttInInfo("testUrl2", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN, 26f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN, 20f))),
                                               LocalTime.of(0, 1));
        RuleInfo ruleInfo = new RuleInfo(place, deviceName, null, customMode);
        when(deviceService.isAiMode(place, deviceName)).thenReturn(false);
        when(deviceService.isCustomMode(place, deviceName)).thenReturn(true);
        when(occupancyService.getOccupancyStatus(place)).thenReturn(Constants.OCCUPIED);
        when(deviceService.isDevicePowered(deviceName)).thenReturn(false);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        ruleService.createRule(ruleInfo);
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.latestValueFlow", IntegrationFlow.class)).id("latestValueFlow").register();
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.customModeFlow", IntegrationFlow.class)).id("customModeFlow").register();
        MessageChannel channel = applicationContext.getBean("class_b.aircleaner.DirectChannel", MessageChannel.class);
        channel.send(tvoc500Msg);
        channel.send(temp29Msg);

        await().atMost(2, TimeUnit.MINUTES).untilAsserted(() -> verify(messageService).sendDeviceMessage(captor.capture()));
        assertEquals(place, captor.getValue().getPlace());
        assertEquals(deviceName, captor.getValue().getDeviceName());
        assertTrue((Boolean) captor.getValue().getValue());

        flowContext.remove("latestValueFlow");
        flowContext.remove("customModeFlow");
    }

    @Test
    void aiModeEmptyRuleInfo_customMode_off_occupancyFalse() {
        String place = "class_b";
        String deviceName = Constants.AIRCLEANER;
        Message<String> tvoc100Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc", 100);
        Message<String> temp19Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature", 19);
        CustomMode customMode = new CustomMode(false,
                                               Map.of(new MqttInInfo("testUrl", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN_OR_EQUAL, 500f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 200f)),
                                                      new MqttInInfo("testUrl2", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN, 26f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN, 20f))),
                                               LocalTime.of(0, 1));
        RuleInfo ruleInfo = new RuleInfo(place, deviceName, null, customMode);
        when(deviceService.isAiMode(place, deviceName)).thenReturn(false);
        when(deviceService.isCustomMode(place, deviceName)).thenReturn(true);
        when(occupancyService.getOccupancyStatus(place)).thenReturn(Constants.VACANT);
        when(deviceService.isDevicePowered(deviceName)).thenReturn(true);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        ruleService.createRule(ruleInfo);
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.latestValueFlow", IntegrationFlow.class)).id("latestValueFlow").register();
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.customModeFlow", IntegrationFlow.class)).id("customModeFlow").register();
        MessageChannel channel = applicationContext.getBean("class_b.aircleaner.DirectChannel", MessageChannel.class);
        channel.send(tvoc100Msg);
        channel.send(temp19Msg);

        await().atMost(2, TimeUnit.MINUTES).untilAsserted(() -> verify(messageService).sendDeviceMessage(captor.capture()));
        assertEquals(place, captor.getValue().getPlace());
        assertEquals(deviceName, captor.getValue().getDeviceName());
        assertFalse((Boolean) captor.getValue().getValue());

        flowContext.remove("latestValueFlow");
        flowContext.remove("customModeFlow");
    }

    @Test
    void aiModeEmptyRuleInfo_customMode_vacant() {
        String place = "class_b";
        String deviceName = Constants.AIRCLEANER;
        Message<String> tvoc400Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc", 400);
        Message<String> temp19Msg = createMessage("data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature", 19);
        CustomMode customMode = new CustomMode(false,
                                               Map.of(new MqttInInfo("testUrl", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/tvoc"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN_OR_EQUAL, 500f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN_OR_EQUAL, 200f)),
                                                      new MqttInInfo("testUrl2", "data/s/nhnacademy/b/gyeongnam/p/class_b/d/24e124128c067999/e/temperature"),
                                                      new ConditionSet(new CompareCondition(ComparisonOperator.GREATER_THAN, 26f),
                                                                       new CompareCondition(ComparisonOperator.LESS_THAN, 20f))),
                                               LocalTime.of(0, 1));
        RuleInfo ruleInfo = new RuleInfo(place, deviceName, null, customMode);
        when(deviceService.isAiMode(place, deviceName)).thenReturn(false);
        when(deviceService.isCustomMode(place, deviceName)).thenReturn(true);
        when(occupancyService.getOccupancyStatus(place)).thenReturn(Constants.VACANT);
        when(deviceService.isDevicePowered(deviceName)).thenReturn(true);
        ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

        ruleService.createRule(ruleInfo);
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.latestValueFlow", IntegrationFlow.class)).id("latestValueFlow").register();
        flowContext.registration(applicationContext.getBean("class_b.aircleaner.StandardIntegrationFlow.customModeFlow", IntegrationFlow.class)).id("customModeFlow").register();
        MessageChannel channel = applicationContext.getBean("class_b.aircleaner.DirectChannel", MessageChannel.class);
        channel.send(tvoc400Msg);
        channel.send(temp19Msg);

        await().atMost(2, TimeUnit.MINUTES).untilAsserted(() -> verify(messageService).sendDeviceMessage(captor.capture()));
        assertEquals(place, captor.getValue().getPlace());
        assertEquals(deviceName, captor.getValue().getDeviceName());
        assertFalse((Boolean) captor.getValue().getValue());

        flowContext.remove("latestValueFlow");
        flowContext.remove("customModeFlow");
    }


    @Configuration
    static class TestConfig {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    private Message<String> createMessage(String topic, Object value) {
        headersMap.put("mqtt_receivedTopic", topic);
        return new GenericMessage<>("{\"time\":1714029232710,\"value\":" + value + "}", new MessageHeaders(headersMap));
    }
}
