package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
class BatteryLevelServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;

    @Autowired
    private BatteryLevelService batteryLevelService;

    @BeforeEach
    void setUp() {
        batteryLevelService = new BatteryLevelService(redisAdapter);
    }

    @Test
    void setBatteryStatus() {
        batteryLevelService.setBatteryStatus("deviceId", Constants.LOW);
        Mockito.verify(redisAdapter, times(1)).setBatteryStatus("deviceId", Constants.LOW);
    }

    @Test
    void getBatteryStatus() {
        when(redisAdapter.getBatteryStatus("testId")).thenReturn(Constants.CRITICAL);

        String result = batteryLevelService.getBatteryStatus("testId");
        assertThat(result).isEqualTo(Constants.CRITICAL);
    }

    @Test
    void isCriticalLevel() {
        Payload criticalPayload = new Payload(123456L, "10"); // Level at exact critical threshold
        Payload nonCriticalPayload = new Payload(123456L, "11"); // Above critical threshold

        assertThat(batteryLevelService.isCriticalLevel(criticalPayload)).isTrue();
        assertThat(batteryLevelService.isCriticalLevel(nonCriticalPayload)).isFalse();
    }

    @Test
    void isLowLevel() {
        Payload lowPayload = new Payload(123456L, "20");
        Payload nonLowPayload = new Payload(123456L, "8");
        when(redisAdapter.getBatteryStatus("deviceId")).thenReturn(null);
        when(redisAdapter.getBatteryStatus("deviceId2")).thenReturn(Constants.LOW);

        assertThat(batteryLevelService.isLowLevel(lowPayload)).isTrue();
        assertThat(batteryLevelService.isLowLevel(nonLowPayload)).isFalse();
    }

    @Test
    void testAlreadyReportCriticalStatus() {
        when(redisAdapter.getBatteryStatus("deviceId")).thenReturn("critical");
        when(redisAdapter.getBatteryStatus("deviceId2")).thenReturn("low");

        assertTrue(batteryLevelService.alreadyReportCriticalStatus("deviceId"));
        assertFalse(batteryLevelService.alreadyReportCriticalStatus("deviceId2"));
    }

    @Test
    void testAlreadyReportLowStatus() {
        when(redisAdapter.getBatteryStatus("deviceId")).thenReturn("low");
        when(redisAdapter.getBatteryStatus("deviceId2")).thenReturn(null);

        assertTrue(batteryLevelService.alreadyReportLowStatus("deviceId"));
        assertFalse(batteryLevelService.alreadyReportLowStatus("deviceId2"));
    }


}