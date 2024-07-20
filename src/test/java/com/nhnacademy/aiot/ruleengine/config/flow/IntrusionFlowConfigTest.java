// package com.nhnacademy.aiot.ruleengine.config.flow;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.nhnacademy.aiot.ruleengine.dto.message.ValueMessage;
// import com.nhnacademy.aiot.ruleengine.service.IntrusionService;
// import com.nhnacademy.aiot.ruleengine.service.MessageService;
// import com.nhnacademy.aiot.ruleengine.service.MqttService;
// import com.nhnacademy.aiot.ruleengine.service.SensorService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.integration.channel.DirectChannel;
// import org.springframework.integration.config.EnableIntegration;
// import org.springframework.integration.dsl.IntegrationFlow;
// import org.springframework.integration.dsl.context.IntegrationFlowContext;
// import org.springframework.messaging.Message;
// import org.springframework.messaging.MessageChannel;
// import org.springframework.messaging.support.GenericMessage;
// import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// import java.time.LocalTime;

// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentCaptor.forClass;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @EnableIntegration
// @SpringJUnitConfig(classes = {IntrusionFlowConfig.class, SensorService.class, IntrusionFlowConfigTest.TestConfig.class})
// class IntrusionFlowConfigTest {

//     @Autowired
//     private IntegrationFlowContext flowContext;
//     @Autowired
//     private IntegrationFlow intrusionProcess;
//     @Autowired
//     private MessageChannel intrusionChannel;
//     @MockBean
//     private MessageService messageService;
//     @MockBean
//     private IntrusionService intrusionService;
//     @MockBean
//     private MqttService mqttService;

//     @BeforeEach
//     void setUp() {
//         if (!flowContext.getRegistry().containsKey("intrusionProcess")) {
//             flowContext.registration(intrusionProcess).id("intrusionProcess").register();
//         }
//         doNothing().when(messageService).sendDeviceMessage(any(ValueMessage.class));
//     }

//     @Test
//     void alertTimeInactive() {
//         Message<String> occupied = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
//         Message<String> vacant = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"vacant\"}");
//         when(intrusionService.isAlertTimeActive(any(LocalTime.class))).thenReturn(false);

//         intrusionChannel.send(occupied);
//         intrusionChannel.send(vacant);

//         verify(messageService, never()).sendDeviceMessage(any(ValueMessage.class));
//     }

//     @Test
//     void alertTimeActive() {
//         Message<String> occupied = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"occupied\"}");
//         Message<String> vacant = new GenericMessage<>("{\"time\":1714029000000,\"value\":\"vacant\"}");
//         when(intrusionService.isAlertTimeActive(any(LocalTime.class))).thenReturn(true);
//         ArgumentCaptor<ValueMessage> captor = forClass(ValueMessage.class);

//         intrusionChannel.send(occupied);
//         intrusionChannel.send(vacant);

//         verify(messageService).sendDeviceMessage(captor.capture());
//         assertTrue((Boolean) captor.getValue().getValue());
//     }

//     @Configuration
//     static class TestConfig {
//         @Bean
//         MessageChannel intrusionChannel() {
//             return new DirectChannel();
//         }

//         @Bean
//         ObjectMapper objectMapper() {
//             return new ObjectMapper();
//         }
//     }
// }
