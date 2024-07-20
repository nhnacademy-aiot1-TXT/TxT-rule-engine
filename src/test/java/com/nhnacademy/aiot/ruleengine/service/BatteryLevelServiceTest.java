package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = BatteryLevelService.class)
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
        doNothing().when(redisAdapter).setValue("battery_status:deviceId", Constants.LOW);

        batteryLevelService.setBatteryStatus("deviceId", Constants.LOW);

        Mockito.verify(redisAdapter).setValue("battery_status:deviceId", Constants.LOW);
    }

    @Test
    void getBatteryStatus() {
        when(redisAdapter.getStringValue("battery_status:testId")).thenReturn(Constants.CRITICAL);

        String result = batteryLevelService.getBatteryStatus("testId");

        assertEquals(Constants.CRITICAL, result);
    }

    @Test
    void isCriticalLevel() {
        Payload criticalPayload = new Payload(123456L, "10"); // Level at exact critical threshold
        Payload nonCriticalPayload = new Payload(123456L, "11"); // Above critical threshold

        boolean criticalLevel = batteryLevelService.isCriticalLevel(criticalPayload);
        boolean nonCriticalLevel = batteryLevelService.isCriticalLevel(nonCriticalPayload);

        assertTrue(criticalLevel);
        assertFalse(nonCriticalLevel);
    }

    @Test
    void isLowLevel() {
        Payload lowPayload = new Payload(123456L, "20");
        Payload nonLowPayload = new Payload(123456L, "8");
        when(redisAdapter.getStringValue("deviceId")).thenReturn(null);
        when(redisAdapter.getStringValue("deviceId2")).thenReturn(Constants.LOW);

        boolean lowLevel = batteryLevelService.isLowLevel(lowPayload);
        boolean nonLowLevel = batteryLevelService.isLowLevel(nonLowPayload);

        assertTrue(lowLevel);
        assertFalse(nonLowLevel);
    }

    @Test
    void testAlreadyReportCriticalStatus() {
        when(redisAdapter.getStringValue("battery_status:deviceId")).thenReturn(Constants.CRITICAL);
        when(redisAdapter.getStringValue("battery_status:deviceId2")).thenReturn(Constants.LOW);

        boolean deviceId = batteryLevelService.alreadyReportCriticalStatus("deviceId");
        boolean deviceId2 = batteryLevelService.alreadyReportCriticalStatus("deviceId2");

        assertTrue(deviceId);
        assertFalse(deviceId2);
    }

    @Test
    void testAlreadyReportLowStatus() {
        when(redisAdapter.getStringValue("battery_status:deviceId")).thenReturn(Constants.LOW);
        when(redisAdapter.getStringValue("battery_status:deviceId2")).thenReturn(null);

        boolean deviceId = batteryLevelService.alreadyReportLowStatus("deviceId");
        boolean deviceId2 = batteryLevelService.alreadyReportLowStatus("deviceId2");

        assertTrue(deviceId);
        assertFalse(deviceId2);
    }
}
