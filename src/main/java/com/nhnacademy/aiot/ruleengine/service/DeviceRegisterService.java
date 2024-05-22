package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.rule.AiModeDto;
import com.nhnacademy.aiot.ruleengine.dto.rule.CustomModeDto;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleDto;

public interface DeviceRegisterService {

    RuleDto parseDeviceRegisterInfo(String deviceRegisterInfo);

    AiModeDto extractAiModeDto(Object aiModeDtoData);

    CustomModeDto extractCustomModeDto(Object customModeDtoData);
}
