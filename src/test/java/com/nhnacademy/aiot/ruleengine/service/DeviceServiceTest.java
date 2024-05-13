package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;

@SpringJUnitConfig(classes = DeviceService.class)
class DeviceServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(redisAdapter);
    }

    @Test
    void testIsAirConditionerPowered() {
        Mockito.when(redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCONDITIONER))
               .thenReturn(true);

        boolean result = deviceService.isAirConditionerPowered();

        assertTrue(result);
    }

    @Test
    void testIsAirCleanerPowered() {
        Mockito.when(redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCLEANER))
               .thenReturn(false);

        boolean result = deviceService.isAirCleanerPowered();

        assertFalse(result);
    }

    @Test
    void testIsLightPowered() {
        Mockito.when(redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.LIGHT)).thenReturn(true);

        boolean result = deviceService.isLightPowered();

        assertTrue(result);
    }

    @Test
    void testSetAirConditionerPower() {
        doNothing().when(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCONDITIONER, true);

        deviceService.setAirConditionerPower(true);

        Mockito.verify(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCONDITIONER, true);
    }

    @Test
    void testSetAirCleanerPower() {
        doNothing().when(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCLEANER, false);

        deviceService.setAirCleanerPower(false);

        Mockito.verify(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCLEANER, false);
    }

    @Test
    void testSetLightPower() {
        doNothing().when(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.LIGHT, true);

        deviceService.setLightPower(true);

        Mockito.verify(redisAdapter).setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.LIGHT, true);
    }

    @Test
    void testIsAutoMode() {
        Mockito.when(redisAdapter.getBooleanValue(Constants.AUTO_MODE)).thenReturn(true);

        boolean result = deviceService.isAutoMode();

        assertTrue(result);
    }
}
