package com.nhnacademy.aiot.ruleengine.controller;

import com.nhnacademy.aiot.ruleengine.domain.SwitchState;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * RabbitMQ 테스트용 컨트롤러 (기능 구현 시, 삭제할 예정)
 * @author jjunho50
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class TestController {

    private final MessageService messageService;

    /**
     * 실제로 서비스할 컨트롤러가 아님!!!! (테스트용)
     *
     * @param switchState 발행할 메시지의 DTO 객체
     * @return ResponseEntity 객체로 응답을 반환
     */
    @PostMapping(value = "/light")
    public ResponseEntity<?> sendLightStateMessage(@RequestBody SwitchState switchState) {
        messageService.sendLightMessage(switchState);
        return ResponseEntity.ok("Light_StateMessage sent to RabbitMQ!");
    }

    @PostMapping(value = "/air-cleaner")
    public ResponseEntity<?> sendAircleanerStateMessage(@RequestBody SwitchState switchState) {
        messageService.sendAircleanerMessage(switchState);
        return ResponseEntity.ok("Air_Cleaner_StateMessage sent to RabbitMQ!");
    }

    @PostMapping(value = "/air-conditioner")
    public ResponseEntity<?> sendAirconditionerStateMessage(@RequestBody SwitchState switchState) {
        messageService.sendAirconditionerMessage(switchState);
        return ResponseEntity.ok("Air_Conditioner_StateMessage sent to RabbitMQ!");
    }
}
