package com.nhnacademy.aiot.ruleengine.listener;

import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.StaticApplicationContext;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServerClosedMessageSenderTest {

    @Mock
    private MessageSender messageSender;
    @InjectMocks
    private ServerClosedMessageSender serverClosedMessageSender;

    @Test
    void onApplicationEvent() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();

        ContextClosedEvent contextClosedEvent = new ContextClosedEvent(context);
        serverClosedMessageSender.onApplicationEvent(contextClosedEvent);

        verify(messageSender).send("RuleEngine", "룰엔진 서버가 종료되었습니다.");
    }
}