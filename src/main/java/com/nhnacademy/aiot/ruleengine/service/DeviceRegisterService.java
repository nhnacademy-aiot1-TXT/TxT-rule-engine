package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.dto.rule.AiMode;
import com.nhnacademy.aiot.ruleengine.dto.rule.CustomMode;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;

public interface DeviceRegisterService {

    RuleInfo parseDeviceRegisterInfo(String deviceRegisterInfo);

    AiMode extractAiMode(Object aiModeDtoData);

    CustomMode extractCustomMode(Object customModeDtoData);
}
