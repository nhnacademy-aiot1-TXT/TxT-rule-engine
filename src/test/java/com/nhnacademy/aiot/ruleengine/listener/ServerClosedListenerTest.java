//package com.nhnacademy.aiot.ruleengine.listener;
//
//import com.nhnacademy.aiot.ruleengine.send.MessageSender;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.springframework.context.event.ContextClosedEvent;
//import org.springframework.core.env.Environment;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class ServerClosedListenerTest {
//    @Test
//    public void whenServerClosed_thenSendMessage() {
//        MessageSender messageSender = mock(MessageSender.class);
//        ServerClosedListener listener = new ServerClosedListener(messageSender);
//        ContextClosedEvent event = mock(ContextClosedEvent.class);
//        Environment environment = mock(Environment.class);
//
//        when(event.getApplicationContext().getEnvironment()).thenReturn(environment);
//        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
//
//        listener.onApplicationEvent(event);
//
//        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//        verify(messageSender).send(eq("RuleEngine"), captor.capture());
//
//        String actualMessage = captor.getValue();
//        assertEquals("룰엔진 서버가 종료되었습니다.", actualMessage);
//    }
//
//    @Test
//    public void whenServerClosedWithTestProfile_thenDoNotSendMessage() {
//        MessageSender messageSender = mock(MessageSender.class);
//        ServerClosedListener listener = new ServerClosedListener(messageSender);
//        ContextClosedEvent event = mock(ContextClosedEvent.class);
//        Environment environment = mock(Environment.class);
//
//        when(event.getApplicationContext().getEnvironment()).thenReturn(environment);
//        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
//
//        listener.onApplicationEvent(event);
//
//        verifyNoInteractions(messageSender);
//    }
//}