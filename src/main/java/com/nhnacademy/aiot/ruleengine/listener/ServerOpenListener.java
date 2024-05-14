package com.nhnacademy.aiot.ruleengine.listener;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerOpenListener implements ApplicationListener<ApplicationReadyEvent> {
    private final MessageSender messageSender;

    public ServerOpenListener(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        String activeProfile = event.getApplicationContext().getEnvironment().getActiveProfiles()[0];

        if (!activeProfile.equals("test"))
            messageSender.send(Constants.RULE_ENGINE, Constants.RULE_ENGINE_OPEN_MESSAGE);
    }
}