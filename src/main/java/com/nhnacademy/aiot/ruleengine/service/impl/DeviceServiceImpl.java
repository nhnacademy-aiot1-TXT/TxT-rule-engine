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
    public RuleDto parseDeviceRegisterInfo(String deviceRegisterInfo) {

        try {
            Map<String, Object> requestMapData = objectMapper.readValue(deviceRegisterInfo, Map.class);

            AiModeDto aiModeDto = extractAiModeDto(requestMapData.get("aiModeDto"));
            CustomModeDto customModeDto = extractCustomModeDto(requestMapData.get("customModeDto"));

            RuleDto ruleDto = new RuleDto(requestMapData.get("place").toString(),
                    requestMapData.get("deviceName").toString(),
                    Boolean.parseBoolean(requestMapData.get("occupancyCheckRequired").toString()),
                    aiModeDto,
                    customModeDto);

            System.out.println("RuleDto: " + ruleDto);

            return ruleDto;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AiModeDto extractAiModeDto(Object aiModeDtoData) {
        try {
            String aiModeJson = objectMapper.writeValueAsString(aiModeDtoData);
            Map<String, Object> aiModeMap = objectMapper.readValue(aiModeJson, Map.class);

            List<MqttInDto> mqttInDtos = objectMapper.convertValue(aiModeMap.get("mqttInDtos"), List.class);
            int aiModeHour = Integer.parseInt(aiModeMap.get("hour").toString());
            int aiModeMinutes = Integer.parseInt(aiModeMap.get("minutes").toString());
            LocalTime aiModeTimeInterval = LocalTime.of(aiModeHour, aiModeMinutes);

            return new AiModeDto(mqttInDtos, aiModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomModeDto extractCustomModeDto(Object customModeDtoData) {
        try {
            String customModeJson = objectMapper.writeValueAsString(customModeDtoData);

            Map<String, Object> customModeMap = objectMapper.readValue(customModeJson, Map.class);
            Map<String, Object> mqttConditonMap = (Map<String, Object>) customModeMap.get("mqttConditionMap");

            Map<MqttInDto, ConditionSetDto> mqttConditionMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : mqttConditonMap.entrySet()) {
                MqttInDto key = objectMapper.readValue(entry.getKey(), MqttInDto.class);
                ConditionSetDto value = objectMapper.convertValue(entry.getValue(), ConditionSetDto.class);
                mqttConditionMap.put(key, value);
            }

            int customModeHour = Integer.parseInt(customModeMap.get("hour").toString());
            int customModeMinutes = Integer.parseInt(customModeMap.get("minutes").toString());
            LocalTime customModeTimeInterval = LocalTime.of(customModeHour, customModeMinutes);

            return new CustomModeDto(mqttConditionMap, customModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
