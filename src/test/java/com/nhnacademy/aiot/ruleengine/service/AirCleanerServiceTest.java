package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = AirCleanerService.class)
class AirCleanerServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;
    private AirCleanerService airCleanerService;

    @BeforeEach
    void setUp() {
        airCleanerService = new AirCleanerService(redisAdapter);
    }

    @Test
    void testSetTimer() {
        Payload payload = new Payload(1713406102466L, "40.3");
        Mockito.when(redisAdapter.hasKey(Constants.AIRCLEANER + Constants.TIMER)).thenReturn(false);

        Payload result = airCleanerService.setTimer(payload);

        assertEquals(payload, result);
        Mockito.verify(redisAdapter).setValue(Constants.AIRCLEANER + Constants.TIMER, payload.getTime());
    }

    @Test
    void testSaveVoc() {
        Payload payload = new Payload(1713406102466L, "450");

        Payload result = airCleanerService.saveVoc(payload);

        assertEquals(payload, result);
        Mockito.verify(redisAdapter).saveFloatToList(Constants.VOC, payload.getValue());
    }

    @Test
    void testGetAvg() {
        List<Double> list = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Mockito.when(redisAdapter.getAllDoubleList(Constants.VOC)).thenReturn(list);

        double result = airCleanerService.getAvg();

        assertEquals(3.0, result);
    }

    @Test
    void testDeleteListAndTimer() {
        airCleanerService.deleteListAndTimer();

        Mockito.verify(redisAdapter).delete(Constants.VOC);
        Mockito.verify(redisAdapter).delete(Constants.AIRCLEANER + Constants.TIMER);
    }

    @Test
    void testIsTimerActive() {
        Payload payload = new Payload(1000L, "40.5");

        boolean result = airCleanerService.isTimerActive(payload);

        assertTrue(result);
    }
}
