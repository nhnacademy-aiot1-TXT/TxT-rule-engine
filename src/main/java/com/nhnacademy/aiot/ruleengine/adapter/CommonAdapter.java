package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorRequest;
import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "common-api")
public interface CommonAdapter {
    @GetMapping("/api/common/time-interval")
    TimeIntervalResponse getTimeIntervalBySensorName(@RequestParam("sensorName") String sensorName);

    @PutMapping("/api/common/device-sensor")
    ResponseEntity<DeviceSensorResponse> updateSensorByDeviceAndSensor(@RequestBody DeviceSensorRequest deviceSensorRequest);

    @PostMapping("/api/common/device-sensor")
    ResponseEntity<DeviceSensorResponse> addSensor(@RequestBody DeviceSensorRequest deviceSensorRequest);

    @DeleteMapping("/api/common/device-sensor/{placeCode}/{deviceName}")
    ResponseEntity<Void> deleteSensorsByPlaceAndDevice(@PathVariable String placeCode, @PathVariable String deviceName);
}
