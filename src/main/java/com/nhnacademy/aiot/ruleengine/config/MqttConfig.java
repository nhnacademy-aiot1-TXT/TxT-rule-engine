package com.nhnacademy.aiot.ruleengine.config;

import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import com.nhnacademy.aiot.ruleengine.service.MessageService;
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


/**
 * MQTT와 관련된 설정을 정의하는 클래스
 *
 * @author jjunho50
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("ConstantConditions")
public class MqttConfig {

    private final InfluxService influxService;
    private final MessageService messageService;

    @Bean
    public MessageChannel influxInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel vocChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel occupancyChannel() {
        return new DirectChannel();
    }


    private MqttPahoMessageDrivenChannelAdapter createMqttAdapter(String url, String clientId, String... topic) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(url, clientId, topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        return adapter;
    }

    @Bean
    public MessageProducer occupancySensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter("tcp://133.186.229.200:1883",
                "rule-engine-occupancy",
                "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/occupancy");
        adapter.setOutputChannel(occupancyChannel());
        return adapter;
    }

    @Bean
    public MessageProducer vocSensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter("tcp://133.186.229.200:1883",
                "rule-engine-voc",
                "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/tvoc");
        adapter.setOutputChannel(vocChannel());
        return adapter;
    }

    /**
     * MQTT 메시지를 처리하는 MessageHandler 빈을 생성하고 반환합니다.
     * 이 메소드는 TxT 팀의 커스텀 MQTT 메시지를 InfluxDB에 저장합니다.
     *
     * @return MessageHandler 객체
     */
    @Bean
    @ServiceActivator(inputChannel = "influxInputChannel")
    public MessageHandler handler1() {
        return message -> {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            String payload = message.getPayload().toString();

            influxService.save(topic, payload);
            messageService.sendValidateMessage(topic, payload);
        };
    }

    //
//    /**
//     * 이 메소드는 TxT 팀이 별도로 설치한 센서 메시지를 수신하는 데 필요한 설정을 정의하며,
//     * 수신된 메시지는 txtSensorInputChannel을 통해 전달됩니다.
//     *
//     * @return MessageProducer 객체
//     */
//    @Bean
//    public MessageProducer txtSensorInbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.229.200:1883", "rule-engine-txt",
//                        "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/total_people_count",
//                        "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/magnet_status",
//                        "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(2);
//        adapter.setOutputChannel(dataInputChannel());
//        return adapter;
//    }
//
//    /**
//     * 이 메소드는 학원의 기존 센서들의 메시지를 수신하는 데 필요한 설정을 정의
//     *
//     * @return MessageProducer 객체
//     */
//    @Bean
//    public MessageProducer academySensorInbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter("tcp://133.186.153.19:1883", "rule-engine-academy",
//                        "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/co2",
//                     ,
//                        "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/humidity",
//                        "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(2);
//        adapter.setOutputChannel(dataInputChannel());
//        return adapter;
//    }
}
