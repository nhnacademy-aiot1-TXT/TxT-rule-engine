package com.nhnacademy.aiot.ruleengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleDto;
import com.nhnacademy.aiot.ruleengine.service.DeviceRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/rule")
public class DeviceRegisterController {
    private final DeviceRegisterService deviceRegisterService;

    private final ObjectMapper objectMapper;
    private final MessageConverter messageConverter;

    @PostMapping(value = "/device/register")
    public ResponseEntity<Void> registerRuleInfoFlow(@RequestBody String deviceRegisterInfo) {
        RuleDto ruleDto = deviceRegisterService.parseDeviceRegisterInfo(deviceRegisterInfo);
        return ResponseEntity.ok().build();
    }

}

