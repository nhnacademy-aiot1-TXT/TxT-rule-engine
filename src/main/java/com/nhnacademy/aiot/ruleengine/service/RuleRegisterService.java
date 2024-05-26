package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.rule.AiMode;
import com.nhnacademy.aiot.ruleengine.dto.rule.CustomMode;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;

/**
 * RuleRegisterService 인터페이스.
 * 디바이스 등록 정보를 파싱하고 AI 모드 및 커스텀 모드를 추출하는 메서드를 정의합니다.
 */
public interface RuleRegisterService {

    RuleInfo parseDeviceRegisterInfo(String deviceRegisterInfo);

    AiMode extractAiMode(Object aiModeDtoData);

    CustomMode extractCustomMode(Object customModeDtoData);
}
