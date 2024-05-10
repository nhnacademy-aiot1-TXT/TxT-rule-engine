package com.nhnacademy.aiot.ruleengine.config;

import com.nhn.dooray.client.DoorayHookSender;
import com.nhnacademy.aiot.ruleengine.send.DoorayMessageSender;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration

public class DoorayMessageConfig {

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Bean
    @ConditionalOnClass(DoorayHookSender.class)
    @ConditionalOnProperty(value = "message.dooray.hook-url")
    public DoorayHookSender doorayHookSender(RestTemplate restTemplate, @Value("${message.dooray.hook-url}") String url) {
        return new DoorayHookSender(restTemplate, url);
    }

    @Bean
    @ConditionalOnBean(DoorayHookSender.class)
    public MessageSender messageSender(DoorayHookSender doorayHookSender) {
        return new DoorayMessageSender(doorayHookSender);
    }
}
