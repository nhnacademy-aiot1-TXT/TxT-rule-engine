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

    @Value("${rabbitmq.exchange.sensor.name}")
    private String exchangeSensorName;

    @Value("${rabbitmq.device.queue.name}")
    private String deviceQueue;

    @Value("${rabbitmq.device.routing.key}")
    private String deviceRouterKey;

    @Value("${rabbitmq.intrusion.queue.name}")
    private String intrusionQueue;

    @Value("${rabbitmq.intrusion.routing.key}")
    private String intrusionRouterKey;

    @Value("${rabbitmq.occupancy.queue.name}")
    private String occupancyQueue;

    @Value("${rabbitmq.occupancy.routing.key}")
    private String occupancyRouterKey;

    @Value("${rabbitmq.battery.queue.name}")
    private String batteryQueue;

    @Value("${rabbitmq.battery.routing.key}")
    private String batteryRouterKey;

    @Value("${rabbitmq.predict.queue.name}")
    private String predictQueue;

    @Value("${rabbitmq.predict.routing.key}")
    private String predictRouterKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange sensorExchange() {
        return new DirectExchange(exchangeSensorName);
    }

    @Bean
    public Queue deviceQueue() {
        return new Queue(deviceQueue);
    }

    @Bean
    public Binding deviceBinding(Queue deviceQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deviceQueue).to(exchange).with(deviceRouterKey);
    }

    @Bean
    public Queue intrusionQueue() {
        return new Queue(intrusionQueue);
    }

    @Bean
    public Binding intrusionBinding(Queue intrusionQueue, DirectExchange exchange) {
        return BindingBuilder.bind(intrusionQueue).to(exchange).with(intrusionRouterKey);
    }

    @Bean
    public Queue occupancyQueue() {
        return new Queue(occupancyQueue);
    }

    @Bean
    public Binding occupancyBinding(Queue occupancyQueue, DirectExchange sensorExchange) {
        return BindingBuilder.bind(occupancyQueue).to(sensorExchange).with(occupancyRouterKey);
    }

    @Bean
    public Queue batteryQueue() {
        return new Queue(batteryQueue);
    }

    @Bean
    public Binding batteryBinding(Queue batteryQueue, DirectExchange sensorExchange) {
        return BindingBuilder.bind(batteryQueue).to(sensorExchange).with(batteryRouterKey);
    }

    @Bean
    public Queue predictQueue() {
        return new Queue(predictQueue);
    }

    @Bean
    public Binding predictBinding(Queue predictQueue, DirectExchange sensorExchange) {
        return BindingBuilder.bind(predictQueue).to(sensorExchange).with(predictRouterKey);
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
