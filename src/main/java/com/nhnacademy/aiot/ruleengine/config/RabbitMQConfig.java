package com.nhnacademy.aiot.ruleengine.config;

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

/**
 * RabbitMQ 서비스와 관련된 설정을 정의하는 클래스
 *
 * @author jjunho50
 */
@Configuration
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

    @Value("${rabbitmq.aircleaner.queue.name}")
    private String aircleanerQueue;

    @Value("${rabbitmq.aircleaner.routing.key}")
    private String aircleanerRouterKey;

    @Value("${rabbitmq.light.queue.name}")
    private String lightQueue;

    @Value("${rabbitmq.light.routing.key}")
    private String lightRouterKey;

    @Value("${rabbitmq.airconditioner.queue.name}")
    private String airconditionerQueue;

    @Value("${rabbitmq.airconditioner.routing.key}")
    private String airconditionerRouterKey;


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }


    @Bean
    public Queue aircleanerQueue() {
        return new Queue(aircleanerQueue);
    }

    @Bean
    public Binding aircleanerBinding(Queue aircleanerQueue, DirectExchange exchange) {
        return BindingBuilder.bind(aircleanerQueue).to(exchange).with(aircleanerRouterKey);
    }

    @Bean
    public Queue lightQueue() {
        return new Queue(lightQueue);
    }

    @Bean
    public Binding lightBinding(Queue lightQueue, DirectExchange exchange) {
        return BindingBuilder.bind(lightQueue).to(exchange).with(lightRouterKey);
    }

    @Bean
    public Queue airconditionerQueue() {
        return new Queue(airconditionerQueue);
    }

    @Bean
    public Binding airconditionerBinding(Queue airconditionerQueue, DirectExchange exchange) {
        return BindingBuilder.bind(airconditionerQueue).to(exchange).with(airconditionerRouterKey);
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
