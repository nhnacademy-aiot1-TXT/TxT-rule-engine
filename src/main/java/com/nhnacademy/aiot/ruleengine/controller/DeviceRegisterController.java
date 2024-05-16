package com.nhnacademy.aiot.ruleengine.controller;

import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;
import com.nhnacademy.aiot.ruleengine.service.DeviceRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/device")
public class DeviceRegisterController {
    private final DeviceRegisterService deviceRegisterService;

    @PostMapping("/register")
    public ResponseEntity<String> registerRuleInfoFlow(@RequestBody RuleInfo ruleInfo) {
        deviceRegisterService.registerDevice(ruleInfo);
        return ResponseEntity.ok("Flow registered successfully!");
    }

}

