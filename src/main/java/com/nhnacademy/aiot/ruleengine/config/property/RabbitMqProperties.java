package com.nhnacademy.aiot.ruleengine.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {
    private final List<BindingProperty> bindings = new ArrayList<>();

    @Getter
    @Setter
    public static class BindingProperty {
        private String queueName;
        private String routingKey;
    }
}
