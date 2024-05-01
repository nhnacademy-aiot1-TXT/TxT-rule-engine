package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "common-api", path = "/api/common/device-sensor")
public interface CommonAdapter {

    @GetMapping("/{deviceId}/{sensorId}")
    DeviceSensorResponse getOnOffValue(@PathVariable("deviceId") Long deviceId, @PathVariable("sensorId") Long sensorId);
}
