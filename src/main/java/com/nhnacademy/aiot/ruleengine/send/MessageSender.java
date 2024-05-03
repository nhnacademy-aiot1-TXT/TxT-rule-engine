package com.nhnacademy.aiot.ruleengine.send;

public interface MessageSender {
    void send(String botName, String message);
}
