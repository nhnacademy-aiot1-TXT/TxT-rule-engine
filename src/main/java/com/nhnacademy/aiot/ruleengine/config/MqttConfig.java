package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Slf4j
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
                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.229.200:1883", "rule-engine-txt1",
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
                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.153.19:1883", "rule-engine-academy2",
                        "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/+");
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
            System.out.println(message.getPayload());
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "academySensorInputChannel")
    public MessageHandler handler2() {
        return message -> {
            influxService.saveData(
                    message.getHeaders().get("mqtt_receivedTopic", String.class),
                    message.getPayload().toString());
            System.out.println(message.getPayload());
        };
    }
}
