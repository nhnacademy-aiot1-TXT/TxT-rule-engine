package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceRegisterService {
    private final RedisAdapter redisAdapter;

    public void registerDevice(RuleInfo ruleInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRuleInfo = objectMapper.writeValueAsString(ruleInfo);
            System.out.println("RuleInfo to JSON: " + jsonRuleInfo);

            saveDeviceInfo(ruleInfo.getPlace(), ruleInfo.getDeviceName(), jsonRuleInfo);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDeviceInfo(String place, String deviceName, String jsonRuleInfo) {
        redisAdapter.setValue(place + ":" + deviceName, jsonRuleInfo);
    }
}


