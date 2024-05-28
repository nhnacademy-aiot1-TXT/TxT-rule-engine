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

/**
 * Rule 등록 요청을 처리하는 컨트롤러 클래스.
 * 이 클래스는 Rule 등록 데이터를 받아서 처리하고, 플로우 등록을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/rule")
public class RuleRegisterController {
    private final RuleRegisterService ruleRegisterService;
    private final RuleService ruleService;

    /**
     * 룰 정보 플로우를 등록하는 엔드포인트.
     *
     * @param ruleRegisterInfo 프론트서버에서 전달된 Rule 등록 요청 정보.
     * @return 플로우가 성공적으로 등록되었을 경우 성공 메시지를 포함한 ResponseEntity,
     * 등록이 실패할 경우 오류 메시지를 포함한 ResponseEntity.
     */
    @PostMapping(value = "/device/register")
    public ResponseEntity<String> registerRuleInfoFlow(@RequestBody String ruleRegisterInfo) {
        try {
            RuleInfo ruleInfo = ruleRegisterService.parseRuleRegisterInfo(ruleRegisterInfo);

            ruleService.updateRule(ruleInfo);

            return ResponseEntity.ok("Flow registered successfully!");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Flow registration failed: " + e.getMessage());
        }
    }

}

