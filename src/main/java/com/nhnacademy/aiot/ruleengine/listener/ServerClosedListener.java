package com.nhnacademy.aiot.ruleengine.listener;

import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerClosedListener implements ApplicationListener<ContextClosedEvent> {
    private final MessageSender messageSender;

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        String activeProfile = event.getApplicationContext().getEnvironment().getActiveProfiles()[0];

        if (!activeProfile.equals("test")) {
            messageSender.send("RuleEngine", "룰엔진 서버가 종료되었습니다.");
        }
    }
}