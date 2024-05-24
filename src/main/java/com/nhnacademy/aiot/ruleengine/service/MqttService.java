package com.nhnacademy.aiot.ruleengine.service;

import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    public MqttPahoMessageDrivenChannelAdapter createMqttAdapter(String url, String clientId, MessageChannel outputChannel, String... topic) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(url, clientId, topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(outputChannel);
        return adapter;
    }
}