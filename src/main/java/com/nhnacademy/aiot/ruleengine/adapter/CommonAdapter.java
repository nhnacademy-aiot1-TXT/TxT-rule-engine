package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorRequest;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "common-api", url = "http://133.186.208.129:8400")
public interface CommonAdapter {
    //    @GetMapping("/api/common/device")
    //    DeviceResponse getDeviceByName(@RequestParam("name") String name);
    //
    //    @GetMapping("/api/common/device/devices")
    //    List<DeviceResponse> getDeviceList();

    @PutMapping("/api/common/device-sensor")
    ResponseEntity<DeviceSensorResponse> updateSensorByDeviceAndSensor(@RequestBody DeviceSensorRequest deviceSensorRequest);

    @GetMapping("/api/common/time-interval")
    TimeIntervalResponse getTimeIntervalBySensorName(@RequestParam("sensorName") String sensorName);

    //    @GetMapping("/api/common/device-sensor/{deviceId}/{sensorId}")
    //    DeviceSensorResponse getSensorByDeviceAndSensor(@PathVariable("deviceId") Long deviceId, @PathVariable("sensorId") Long sensorId);
}
