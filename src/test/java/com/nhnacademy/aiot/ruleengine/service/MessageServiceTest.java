package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.domain.SwitchState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
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
    }

    /**
     * 에에컨, 공기청정기, 전등 스위치 ON/OFF 여부 데이터 메세지 처리
     * MessageService의 Field값은 반드시 application.properties의 정보와 일치해야 한다!!!
     *
     * @param routingKey
     */
    @ParameterizedTest
    @ValueSource(strings = {"aircleaner", "light", "airconditioner"})
    public void testSendMessage(String routingKey) {
        SwitchState switchState = new SwitchState();

        switch (routingKey) {
            case "aircleaner":
                messageService.sendAircleanerMessage(switchState);
                break;
            case "light":
                messageService.sendLightMessage(switchState);
                break;
            case "airconditioner":
                messageService.sendAirconditionerMessage(switchState);
                break;
        }
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq(routingKey), any(SwitchState.class));
    }
}