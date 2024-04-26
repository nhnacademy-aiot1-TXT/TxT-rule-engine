package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.config.property.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RabbitMQ 서비스와 관련된 설정을 정의하는 클래스
 *
 * @author jjunho50
 */
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.sensor.name}")
    private String exchangeSensorName;
    private final RabbitMqProperties rabbitMqProperties;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange sensorExchange() {
        return new DirectExchange(exchangeSensorName);
    }

    @Bean
    public List<Queue> queues() {
        return rabbitMqProperties.getBindings().stream()
                .map(binding -> new Queue(binding.getQueueName()))
                .collect(Collectors.toList());
    }

    @Bean
    public List<Binding> bindings() {
        return rabbitMqProperties.getBindings().stream()
                .map(binding -> BindingBuilder.bind(new Queue(binding.getQueueName()))
                        .to(exchange())
                        .with(binding.getRoutingKey()))
                .collect(Collectors.toList());
    }

    /**
     * RabbitMQ 서버와 연결을 위한 ConnectionFactory 빈을 생성하고 반환합니다.
     *
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    /**
     * RabbitTemplate을 생성하여 반환
     * Jackson2JsonMessageConverter를 메시지 컨버터로 설정 (JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정)
     *
     * @param connectionFactory RabbitMQ 와의 연결을 위한 ConnectionFactory 객체
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Jackson 라이브러리를 사용하여 메시지를 JSON 형식으로 변환하는 MessageConverter 빈을 생성
     *
     * @return MessageConverter 객체
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
