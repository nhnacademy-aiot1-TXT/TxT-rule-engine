package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.dto.DeviceResponse;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "common-api", url = "http://133.186.208.129:8400")
public interface CommonAdapter {
    @GetMapping("/api/common/device")
    DeviceResponse getDeviceByName(@RequestParam("name") String name);

    @GetMapping("/api/common/device/devices")
    List<DeviceResponse> getDeviceList();

    @GetMapping("/api/common/time-interval")
    TimeIntervalResponse getTimeIntervalBySensorName(@RequestParam("sensorName") String sensorName);

    @GetMapping("/api/common/device-sensor/{deviceId}/{sensorId}")
    DeviceSensorResponse getSensorByDeviceAndSensor(@PathVariable("deviceId") Long deviceId, @PathVariable("sensorId") Long sensorId);
}
