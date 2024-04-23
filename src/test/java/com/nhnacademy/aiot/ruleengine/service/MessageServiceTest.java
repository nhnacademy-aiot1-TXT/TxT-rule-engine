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
        ReflectionTestUtils.setField(messageService, "exchangeSensorName", "exchangeSensorName");
        ReflectionTestUtils.setField(messageService, "aircleanerRoutingKey", "aircleaner");
        ReflectionTestUtils.setField(messageService, "lightRoutingKey", "light");
        ReflectionTestUtils.setField(messageService, "airconditionerRoutingKey", "airconditioner");
        ReflectionTestUtils.setField(messageService, "batteryRoutingKey", "battery");
        ReflectionTestUtils.setField(messageService, "occupancyRoutingKey", "occupancy");
        ReflectionTestUtils.setField(messageService, "temperatureRoutingKey", "temperature");
        ReflectionTestUtils.setField(messageService, "humidityRoutingKey", "humidity");
        ReflectionTestUtils.setField(messageService, "totalPeopleCountRoutingKey", "totalPeopleCount");
    }

    /**
     * 에에컨, 공기청정기, 전등 스위치 등 디바이스 컨트롤 데이터 메세지 처리 (지금은 에어컨만 존재한다)
     */
    @ParameterizedTest
    @ValueSource(strings = {"airconditioner"})
    public void testDeviceControlMessage(String routingKey) {
        SwitchState switchState = new SwitchState(true);
        String deviceType = "";
        if (routingKey.equals("airconditioner")) {
            deviceType = "magnet_status";
        }

        messageService.sendValidateMessage(deviceType, "open");

        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq(routingKey), eq(switchState));
    }

//    /**
//     * 온습도, 배터리, 재실 인원 수 등 센서 데이터 메세지 처리
//     */
//    @ParameterizedTest
//    @ValueSource(strings = {"occupancy", "battery", "temperature", "humidity", "totalPeopleCount"})
//    public void testSensorDataMessage(String routingKey) throws JsonProcessingException {
//        Payload payload = new Payload(1713406102466L, "45");
//
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonPayload = mapper.writeValueAsString(payload);
//
//        messageService.sendValidateMessage(routingKey, jsonPayload);
//
//        FloatMessage floatMessage;
//        // 행동 확인
//        switch (routingKey) {
//            case "occupancy":
//                SwitchState switchState = new SwitchState(true);
//                verify(rabbitTemplate).convertAndSend(eq("exchangeSensorName"), eq(routingKey), eq(switchState));
//                break;
//            case "battery":
//            case "totalPeopleCount":
//                IntegerMessage integerMessage = new IntegerMessage(45, "magnet_status", "battery_level");
//                verify(rabbitTemplate).convertAndSend(eq("exchangeSensorName"), eq(routingKey), eq(integerMessage));
//                break;
//            case "temperature":
//            case "humidity":
//                floatMessage = new FloatMessage(Float.parseFloat(payload.getValue()), "magnet_status", "battery_level");
//                verify(rabbitTemplate).convertAndSend(eq("exchangeSensorName"), eq(routingKey), eq(floatMessage));
//                break;
//        }
//    }
}
