package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.txt.service.InfluxService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final InfluxService influxService;

    @Bean
    public MessageChannel txtSensorInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel academySensorInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer txtSensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.229.200:1883", "rule-engine",
                        "milesight/s/nhnacademy/b/gyeongnam/p/pair_room/d/+/e/+");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(txtSensorInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer academySensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.153.19:1883", "",
                        "");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(academySensorInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "txtSensorInputChannel")
    public MessageHandler handler1() {
        return message -> {
            influxService.saveData(
                    message.getHeaders().get("mqtt_receivedTopic", String.class),
                    message.getPayload().toString());
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "academySensorInputChannel")
    public MessageHandler handler2() {
        return message -> {

        };
    }
}
