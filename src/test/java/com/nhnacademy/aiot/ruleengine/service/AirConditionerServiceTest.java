package com.nhnacademy.aiot.ruleengine.service;


import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AirConditionerServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;

    @MockBean
    private SensorService sensorService;

    @Test
    public void testSetTimer() {
        Mockito.when(redisAdapter.hasTimer(Constants.AIRCONDITIONER)).thenReturn(false);

        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);

        Payload payload = new Payload(1713406102466L, "25");
        Payload result = airConditionerService.setTimer(Constants.AIRCONDITIONER, payload);

        assertThat(result).isEqualTo(payload);
        Mockito.verify(redisAdapter, Mockito.times(1))
                .setTimer(Constants.AIRCONDITIONER, payload.getTime());
    }

    @Test
    public void testIsIndoorTempMsg() {
        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);

        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_receivedTopic", "test/class_a/temperature");
        Message<String> message = new GenericMessage<>("Test message", headers);

        boolean result = airConditionerService.isIndoorTempMsg(message);

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testIsTimerActive() {
        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);

        Payload payload = new Payload(1713406102466L, "25");
        boolean result = airConditionerService.isTimerActive(Constants.AIRCONDITIONER, payload);

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void testSaveTemperature() {
        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);

        Payload payload = new Payload(1713406102466L, "12.5");

        Payload result = airConditionerService.saveTemperature(payload);

        assertThat(result).isEqualTo(payload);
        Mockito.verify(redisAdapter, Mockito.times(1))
                .saveFloatToList(Constants.TEMPERATURE, payload.getValue());
    }

    @Test
    public void testDeleteForAutoMode() {
        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);
        airConditionerService.deleteForAutoMode();

        Mockito.verify(redisAdapter, Mockito.times(1)).deleteListWithPrefix("airconditioner:");
        Mockito.verify(redisAdapter, Mockito.times(1)).deleteTimer(Constants.AUTO_AIRCONDITIONER);
    }

    @Test
    public void testDeleteListAndTimer() {
        AirConditionerService airConditionerService = new AirConditionerService(redisAdapter, sensorService);
        airConditionerService.deleteListAndTimer();

        Mockito.verify(redisAdapter, Mockito.times(1)).delete(Constants.TEMPERATURE);
        Mockito.verify(redisAdapter, Mockito.times(1)).deleteTimer(Constants.AIRCONDITIONER);
    }
}