package com.nhnacademy.aiot.ruleengine.send;

import com.nhn.dooray.client.DoorayHook;
import com.nhn.dooray.client.DoorayHookSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DoorayMessageSenderTest {

    @Mock
    private DoorayHookSender doorayHookSender;

    @Captor
    private ArgumentCaptor<DoorayHook> doorayHookArgumentCaptor;

    private DoorayMessageSender doorayMessageSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        doorayMessageSender = new DoorayMessageSender(doorayHookSender);
    }

    @Test
    public void send_SendsMessageWithBotNameAndText_WhenCalled() {
        String botName = "bot";
        String message = "message";

        doorayMessageSender.send(botName, message);

        verify(doorayHookSender, times(1)).send(doorayHookArgumentCaptor.capture());

        DoorayHook sentHook = doorayHookArgumentCaptor.getValue();

        assertEquals(botName, sentHook.getBotName());
        assertEquals(message, sentHook.getText());
    }
}