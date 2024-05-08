package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class AirCleanerServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;

    @Test
    public void testSetTimer() {
        Mockito.when(redisAdapter.hasTimer(Constants.AIRCLEANER)).thenReturn(false);
        AirCleanerService airCleanerService = new AirCleanerService(redisAdapter);

        Payload payload = new Payload(1713406102466L, "40.3");
        Payload result = airCleanerService.setTimer(payload);

        assertThat(result).isEqualTo(payload);
        Mockito.verify(redisAdapter, Mockito.times(1)).setTimer(Constants.AIRCLEANER, payload.getTime());
    }

    @Test
    public void testSaveVoc() {
        AirCleanerService airCleanerService = new AirCleanerService(redisAdapter);

        Payload payload = new Payload(1713406102466L, "450");
        Payload result = airCleanerService.saveVoc(payload);

        assertThat(result).isEqualTo(payload);
        Mockito.verify(redisAdapter, Mockito.times(1)).saveFloatToList(Constants.VOC, payload.getValue());
    }

    @Test
    public void testGetAvg() {
        List<Double> list = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Mockito.when(redisAdapter.getAllDoubleList(Constants.VOC)).thenReturn(list);
        AirCleanerService airCleanerService = new AirCleanerService(redisAdapter);

        double result = airCleanerService.getAvg();
        assertThat(result).isEqualTo(3.0);
    }

    @Test
    public void testDeleteListAndTimer() {
        AirCleanerService airCleanerService = new AirCleanerService(redisAdapter);
        airCleanerService.deleteListAndTimer();

        Mockito.verify(redisAdapter, Mockito.times(1)).delete(Constants.VOC);
        Mockito.verify(redisAdapter, Mockito.times(1)).deleteTimer(Constants.AIRCLEANER);
    }

    @Test
    public void testIsTimerActive() {
        AirCleanerService airCleanerService = new AirCleanerService(redisAdapter);

        Payload payload = new Payload(1000L, "40.5");
        boolean result = airCleanerService.isTimerActive(payload);

        assertThat(result).isEqualTo(true);
    }
}