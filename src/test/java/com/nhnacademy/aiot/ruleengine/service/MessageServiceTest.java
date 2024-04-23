package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.SwitchState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * MessageService 테스트 클래스입니다.
 * @author jjunho50
 */
@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(messageService, "exchangeName", "exchange");
        ReflectionTestUtils.setField(messageService, "aircleanerRoutingKey", "aircleaner");
        ReflectionTestUtils.setField(messageService, "lightRoutingKey", "light");
        ReflectionTestUtils.setField(messageService, "airconditionerRoutingKey", "airconditioner");
        ReflectionTestUtils.setField(messageService, "batteryRoutingKey", "battery");
        ReflectionTestUtils.setField(messageService, "occupancyRoutingKey", "occupancy");

    }

    /**
     * 에에컨, 공기청정기, 전등 스위치 ON/OFF 여부 데이터 메세지 처리
     * MessageService의 Field값은 반드시 application.properties의 정보와 일치해야 한다!!!
     */
    @ParameterizedTest
    @ValueSource(strings = {"airconditioner", "occupancy", "battery"})
    public void testSendMessage(String routingKey) {
        SwitchState message = new SwitchState(true);
        String exchangeName = "exchange";
        ReflectionTestUtils.invokeMethod(messageService, "sendMessage", message, routingKey);
        verify(rabbitTemplate).convertAndSend(eq(exchangeName), eq(routingKey), eq(message));
    }
}
