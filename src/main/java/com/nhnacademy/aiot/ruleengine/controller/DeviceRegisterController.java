package com.nhnacademy.aiot.ruleengine.controller;

import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;
import com.nhnacademy.aiot.ruleengine.service.RuleRegisterService;
import com.nhnacademy.aiot.ruleengine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/rule")
public class DeviceRegisterController {
    private final RuleRegisterService deviceRegisterService;
    private final RuleService ruleService;

    @PostMapping(value = "/device/register")
    public ResponseEntity<String> registerRuleInfoFlow(@RequestBody String deviceRegisterInfo) {
        try {
            RuleInfo ruleInfo = deviceRegisterService.parseDeviceRegisterInfo(deviceRegisterInfo);

            //Flow 등록 구현
            ruleService.updateRule(ruleInfo);

            return ResponseEntity.ok("Flow registered successfully!");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Flow registration failed: " + e.getMessage());
        }
    }

}

