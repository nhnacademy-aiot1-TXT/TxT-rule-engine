package com.nhnacademy.aiot.ruleengine.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.rule.*;
import com.nhnacademy.aiot.ruleengine.service.RuleRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RuleRegisterService의 구현체.
 * Rule 등록 정보를 파싱하고, AI 모드 및 커스텀 모드를 추출하여 RuleInfo 객체를 생성합니다.
 */
@Service
@RequiredArgsConstructor
public class RuleRegisterServiceImpl implements RuleRegisterService {
    private final ObjectMapper objectMapper;

    /**
     * 디바이스 등록 정보를 파싱하여 RuleInfo 객체를 생성합니다.
     * AiMode 객체는 null 일 수 있습니다.
     *
     * @param ruleRegisterInfo 프론트서버에서 전달된 Rule 등록 요청 정보.
     * @return 파싱된 RuleInfo 객체
     * @throws RuntimeException JSON 파싱 중 오류 발생 시
     */
    @Override
    public RuleInfo parseRuleRegisterInfo(String ruleRegisterInfo) {

        try {
            Map<String, Object> requestMapData = objectMapper.readValue(ruleRegisterInfo, Map.class);

            AiMode aiMode;
            if (requestMapData.get("aiMode") != null) {
                aiMode = extractAiMode(requestMapData.get("aiMode"));
            } else {
                aiMode = null;
            }

            CustomMode customMode = extractCustomMode(requestMapData.get("customMode"));

            RuleInfo ruleInfo = new RuleInfo(
                    requestMapData.get("place").toString(),
                    requestMapData.get("deviceName").toString(),
                    aiMode,
                    customMode);

            System.out.println("RuleInfo: " + ruleInfo);

            return ruleInfo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AI 모드 데이터를 추출하여 AiMode 객체를 생성합니다.
     * 이 메서드는 JSON 데이터로부터 추출된 Object를 받아 Map으로 변환한 후, 필요한 필드를 추출하여 AiMode 객체를 생성합니다.
     *
     * @param aiModeData AI 모드 데이터
     * @return 파싱된 AiMode 객체
     * @throws RuntimeException JSON 파싱 중 오류 발생 시
     */
    @Override
    public AiMode extractAiMode(Object aiModeData) {
        try {
            String aiModeJson = objectMapper.writeValueAsString(aiModeData);
            Map<String, Object> aiModeMap = objectMapper.readValue(aiModeJson, Map.class);

            List<MqttInInfo> mqttInInfos = objectMapper.convertValue(aiModeMap.get("mqttInInfos"), List.class);
            String hourStr = aiModeMap.get("hour").toString();
            String minutesStr = aiModeMap.get("minutes").toString();

            int aiModeHour = Integer.parseInt(hourStr);
            int aiModeMinutes = Integer.parseInt(minutesStr);
            LocalTime aiModeTimeInterval = LocalTime.of(aiModeHour, aiModeMinutes);

            return new AiMode(mqttInInfos, aiModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 커스텀 모드 데이터를 추출하여 CustomMode 객체를 생성합니다.
     * 이 메서드는 JSON 데이터로부터 추출된 Object를 받아 Map으로 변환한 후, 필요한 필드를 추출하여 CustomMode 객체를 생성합니다.
     *
     * @param customModeData 커스텀 모드 데이터
     * @return 파싱된 CustomMode 객체
     * @throws RuntimeException JSON 파싱 중 오류 발생 시
     */
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

            boolean occupancyCheckRequired = Boolean.parseBoolean(customModeMap.get("occupancyCheckRequired")
                                                                               .toString());
            int customModeHour = Integer.parseInt(customModeMap.get("hour").toString());
            int customModeMinutes = Integer.parseInt(customModeMap.get("minutes").toString());
            LocalTime customModeTimeInterval = LocalTime.of(customModeHour, customModeMinutes);

            return new CustomMode(occupancyCheckRequired, mqttConditionMap, customModeTimeInterval);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
