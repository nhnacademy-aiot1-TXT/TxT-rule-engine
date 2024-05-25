package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = CommonAdapterTest.CustomApplicationRunner.class)
class CommonAdapterTest {

    @Autowired
    private CommonAdapter commonAdapter;

    @Test
    void getTimeIntervalBySensorName() {
        TimeIntervalResponse response = commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY);

        assertEquals(Constants.OCCUPANCY, response.getSensorName());
    }

    @Test
    void updateSensorByDeviceAndSensor() {
        DeviceSensorRequest build = DeviceSensorRequest.builder().deviceName("airConditioner")
                                                       .sensorName("temperature").placeName("class_a")
                                                       .offValue(18f).onValue(26f).build();

        ResponseEntity<DeviceSensorResponse> response = commonAdapter.updateSensorByDeviceAndSensor(build);

        System.out.println(response.toString());
        DeviceSensorResponse body = response.getBody();
        assertEquals(1L, body.getDeviceId());
        assertEquals(1L, body.getSensorId());
        assertEquals("temperature", body.getSensorName());
        assertEquals(26f, body.getOnValue());
        assertEquals(18f, body.getOffValue());
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

