//package com.nhnacademy.aiot.ruleengine.service;
//
//import com.nhnacademy.aiot.ruleengine.dto.message.DetailMessage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class MessageServiceTest {
//    @Mock
//    private RabbitTemplate rabbitTemplate;
//    @InjectMocks
//    private MessageService messageService;
//
//    @BeforeEach
//    public void setup() {
//        ReflectionTestUtils.setField(messageService, "exchangeName", "exchangeName");
//        ReflectionTestUtils.setField(messageService, "exchangeSensorName", "exchangeSensorName");
//        ReflectionTestUtils.setField(messageService, "predictRoutingKey", "predictRoutingKey");
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = {"exchangeSensorName;battery"}, delimiter = ';')
//    void testSendValidateMessage(String exchange, String measurement) {
//        verify(rabbitTemplate, times(1)).convertAndSend(eq(exchange), eq(measurement), any(DetailMessage.class));
//    }
//}
