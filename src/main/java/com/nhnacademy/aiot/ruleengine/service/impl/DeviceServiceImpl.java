package com.nhnacademy.aiot.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.rule.*;
import com.nhnacademy.aiot.ruleengine.service.DeviceRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceRegisterService {
    private final ObjectMapper objectMapper;

    @Override
    public RuleInfo parseDeviceRegisterInfo(String deviceRegisterInfo) {

        try {
            Map<String, Object> requestMapData = objectMapper.readValue(deviceRegisterInfo, Map.class);

            AiMode aiMode = extractAiMode(requestMapData.get("aiMode"));
            CustomMode customMode = extractCustomMode(requestMapData.get("customMode"));

            RuleInfo ruleInfo = new RuleInfo(requestMapData.get("place").toString(),
                                             requestMapData.get("deviceName").toString(),
                                             Boolean.parseBoolean(requestMapData.get("occupancyCheckRequired").toString()),
                                             aiMode,
                                             customMode);

            System.out.println("RuleInfo: " + ruleInfo);

            return ruleInfo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AiMode extractAiMode(Object aiModeData) {
        try {
            String aiModeJson = objectMapper.writeValueAsString(aiModeData);
            Map<String, Object> aiModeMap = objectMapper.readValue(aiModeJson, Map.class);

            List<MqttInInfo> mqttInInfos = objectMapper.convertValue(aiModeMap.get("mqttIns"), List.class);
            int aiModeHour = Integer.parseInt(aiModeMap.get("hour").toString());
            int aiModeMinutes = Integer.parseInt(aiModeMap.get("minutes").toString());
            LocalTime aiModeTimeInterval = LocalTime.of(aiModeHour, aiModeMinutes);

            return new AiMode(mqttInInfos, aiModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomMode extractCustomMode(Object customModeData) {
        try {
            String customModeJson = objectMapper.writeValueAsString(customModeData);

            Map<String, Object> customModeMap = objectMapper.readValue(customModeJson, Map.class);
            Map<String, Object> mqttConditonMap = (Map<String, Object>) customModeMap.get("mqttConditionMap");

            Map<MqttInInfo, ConditionSet> mqttConditionMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : mqttConditonMap.entrySet()) {
                MqttInInfo key = objectMapper.readValue(entry.getKey(), MqttInInfo.class);
                ConditionSet value = objectMapper.convertValue(entry.getValue(), ConditionSet.class);
                mqttConditionMap.put(key, value);
            }

            int customModeHour = Integer.parseInt(customModeMap.get("hour").toString());
            int customModeMinutes = Integer.parseInt(customModeMap.get("minutes").toString());
            LocalTime customModeTimeInterval = LocalTime.of(customModeHour, customModeMinutes);

            return new CustomMode(mqttConditionMap, customModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
