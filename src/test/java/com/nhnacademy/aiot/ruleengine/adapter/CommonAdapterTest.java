package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceResponse;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = CommonAdapterTest.CustomApplicationRunner.class)
class CommonAdapterTest {

    @Autowired
    private CommonAdapter commonAdapter;

    @Test
    void getSensorByDeviceAndSensor() {
        DeviceSensorResponse sensorByDeviceAndSensor = commonAdapter.getSensorByDeviceAndSensor(Constants.AIRCLEANER_DEVICE_ID, Constants.AIRCLEANER_SENSOR_ID);
        assertNotNull(sensorByDeviceAndSensor);
    }


    @Test
    void getDeviceByName() {
        DeviceResponse airconditioner = commonAdapter.getDeviceByName(Constants.AIRCONDITIONER);
        DeviceResponse aircleaner = commonAdapter.getDeviceByName(Constants.AIRCLEANER);
        DeviceResponse light = commonAdapter.getDeviceByName(Constants.LIGHT);

        assertEquals(Constants.AIRCONDITIONER, airconditioner.getDeviceName());
        assertEquals(Constants.AIRCLEANER, aircleaner.getDeviceName());
        assertEquals(Constants.LIGHT, light.getDeviceName());
    }

    @Test
    void getTimeIntervalBySensorName() {
        TimeIntervalResponse response = commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY);

        assertEquals(Constants.OCCUPANCY, response.getSensorName());
    }

    @Test
    void getDeviceList() {
        List<DeviceResponse> deviceList = commonAdapter.getDeviceList();

        assertNotNull(deviceList);
    }

    @EnableFeignClients
    @EnableDiscoveryClient
    @SpringBootApplication
    @ComponentScan(basePackageClasses = CommonAdapter.class,
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = RedisAdapter.class))
    public static class CustomApplicationRunner {
        public static void main(String[] args) {
            SpringApplication.run(CustomApplicationRunner.class, args);
        }
    }

}

