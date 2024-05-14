package com.nhnacademy.aiot.ruleengine.send;

import com.nhn.dooray.client.DoorayHook;
import com.nhn.dooray.client.DoorayHookSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = DoorayMessageSender.class)
class DoorayMessageSenderTest {

    @MockBean
    private DoorayHookSender doorayHookSender;
    @Captor
    private ArgumentCaptor<DoorayHook> doorayHookArgumentCaptor;
    private DoorayMessageSender doorayMessageSender;

    @BeforeEach
    void setUp() {
        doorayMessageSender = new DoorayMessageSender(doorayHookSender);
    }

    @Test
    void send_SendsMessageWithBotNameAndText_WhenCalled() {
        String botName = "bot";
        String message = "message";
        doNothing().when(doorayHookSender).send(any(DoorayHook.class));

        doorayMessageSender.send(botName, message);

        verify(doorayHookSender).send(doorayHookArgumentCaptor.capture());
        assertEquals(botName, doorayHookArgumentCaptor.getValue().getBotName());
        assertEquals(message, doorayHookArgumentCaptor.getValue().getText());
    }
}
