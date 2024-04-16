package com.nhnacademy.aiot.ruleengine.controller;

import com.nhnacademy.aiot.ruleengine.domain.SwitchState;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    /**
     * Queue로 메시지를 발행
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
