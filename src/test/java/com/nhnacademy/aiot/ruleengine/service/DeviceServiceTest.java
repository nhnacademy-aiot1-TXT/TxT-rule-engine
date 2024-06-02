package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = DeviceService.class)
class DeviceServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;
    @Autowired
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testIsAirConditionerPowered() {
        Mockito.when(redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.CLASS_A + "_" + Constants.AIRCONDITIONER))
               .thenReturn(true);

        boolean result = deviceService.isDevicePowered(Constants.CLASS_A, Constants.AIRCONDITIONER);

        assertTrue(result);
    }

    @Test
    void testIsAiMode() {
        Mockito.when(redisAdapter.getBooleanFromHash("ai_mode", Constants.CLASS_A + "_" + Constants.AIRCLEANER)).thenReturn(true);

        boolean result = deviceService.isAiMode(Constants.CLASS_A, Constants.AIRCLEANER);

        assertTrue(result);
    }

    @Test
    void testIsCustomMode() {
        Mockito.when(redisAdapter.getBooleanFromHash("custom_mode", Constants.CLASS_A + "_" + Constants.AIRCLEANER)).thenReturn(true);

        boolean result = deviceService.isCustomMode(Constants.CLASS_A, Constants.AIRCLEANER);

        assertTrue(result);
    }
}
