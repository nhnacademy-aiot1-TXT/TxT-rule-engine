package com.nhnacademy.aiot.ruleengine.send;

import com.nhn.dooray.client.DoorayHook;
import com.nhn.dooray.client.DoorayHookSender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoorayMessageSender implements MessageSender {
    private final DoorayHookSender doorayHookSender;

    @Override
    public void send(String botName, String message) {
        DoorayHook hook = DoorayHook.builder()
                .botName(botName)
                .text(message)
                .build();

        doorayHookSender.send(hook);
    }
}
