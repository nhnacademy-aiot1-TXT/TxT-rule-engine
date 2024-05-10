package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DeviceServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;

    @Test
    public void testIsAirConditionerPowered() {
        Mockito.when(redisAdapter.isDevicePowered("airconditioner")).thenReturn(true);

        DeviceService deviceService = new DeviceService(redisAdapter);
        boolean result = deviceService.isAirConditionerPowered();

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testIsAirCleanerPowered() {
        Mockito.when(redisAdapter.isDevicePowered("aircleaner")).thenReturn(false);

        DeviceService deviceService = new DeviceService(redisAdapter);
        boolean result = deviceService.isAirCleanerPowered();

        assertThat(result).isEqualTo(false);
    }

    @Test
    public void testIsLightPowered() {
        Mockito.when(redisAdapter.isDevicePowered("light")).thenReturn(true);

        DeviceService deviceService = new DeviceService(redisAdapter);
        boolean result = deviceService.isLightPowered();

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testSetAirConditionerPower() {
        DeviceService deviceService = new DeviceService(redisAdapter);
        deviceService.setAirConditionerPower(true);

        Mockito.verify(redisAdapter, Mockito.times(1)).setDevicePower("airconditioner", true);
    }

    @Test
    public void testSetAirCleanerPower() {
        DeviceService deviceService = new DeviceService(redisAdapter);
        deviceService.setAirCleanerPower(false);

        Mockito.verify(redisAdapter, Mockito.times(1)).setDevicePower("aircleaner", false);
    }

    @Test
    public void testSetLightPower() {
        DeviceService deviceService = new DeviceService(redisAdapter);
        deviceService.setLightPower(true);

        Mockito.verify(redisAdapter, Mockito.times(1)).setDevicePower("light", true);
    }

    @Test
    public void testIsAirConditionerAutoMode() {
        Mockito.when(redisAdapter.isDeviceAutoMode("airconditioner")).thenReturn(true);

        DeviceService deviceService = new DeviceService(redisAdapter);
        boolean result = deviceService.isAirConditionerAutoMode();

        assertThat(result).isEqualTo(true);
    }
}