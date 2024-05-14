package com.nhnacademy.aiot.ruleengine.config;

import com.influxdb.exceptions.InfluxException;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
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

/**
 * MQTT와 관련된 설정을 정의하는 클래스
 *
 * @author jjunho50
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final InfluxService influxService;
    private final MessageSender messageSender;

    @Bean
    public MessageChannel influxInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel occupancyChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel airCleanerChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel airConditionerChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel batteryLevelChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel intrusionChannel() {
        return new DirectChannel();
    }

    /**
     * 이 메소드는 TxT 팀이 별도로 설치한 센서 메시지를 수신하는 데 필요한 설정을 정의하며,
     * 수신된 메시지는 txtSensorInputChannel을 통해 전달됩니다.
     *
     * @return MessageProducer 객체
     */
    @Bean
    public MessageProducer txtSensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.TXT_MQTT, "rule-engine-txt",
                                                                        "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/+");
        adapter.setOutputChannel(influxInputChannel());
        return adapter;
    }

    /**
     * 이 메소드는 학원의 기존 센서들의 메시지를 수신하는 데 필요한 설정을 정의
     *
     * @return MessageProducer 객체
     */
    @Bean
    public MessageProducer academySensorInbound() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-academy",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/co2",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/tvoc",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/humidity",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/temperature",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/illumination",
                                  "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
        adapter.setOutputChannel(influxInputChannel());
        return adapter;
    }

    @Bean
    public MessageProducer occupancySensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.TXT_MQTT, "rule-engine-occupancy",
                                                                        "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/vs121/e/occupancy");
        adapter.setOutputChannel(occupancyChannel());
        return adapter;
    }

    @Bean
    public MessageProducer vocSensorInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-voc",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/class_a/d/24e124128c067999/e/tvoc");
        adapter.setOutputChannel(airCleanerChannel());
        return adapter;
    }

    @Bean
    public MessageProducer airConditionerInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-airconditioner",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/class_a/d/+/e/temperature",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/class_a/d/+/e/humidity");
        adapter.setOutputChannel(airConditionerChannel());
        return adapter;
    }

    @Bean
    public MessageProducer airConditionerInbound2() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.TXT_MQTT, "rule-engine-airconditioner2",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/outdoor/d/+/e/temperature",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/outdoor/d/+/e/humidity",
                                                                        "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/+/e/total_people_count");
        adapter.setOutputChannel(airConditionerChannel());
        return adapter;
    }

    /**
     * TxT팀이 설치한 센서들의 batteryLevel 메시지
     */
    @Bean
    public MessageProducer batteryLevelInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.TXT_MQTT, "rule-engine-battery1",
                                                                        "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
        adapter.setOutputChannel(batteryLevelChannel());
        return adapter;
    }

    /**
     * 학원 강의실 A에 설치된 센서들의 batteryLevel 메시지
     */
    @Bean
    public MessageProducer batteryLevelInbound2() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-battery2",
                                                                        "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
        adapter.setOutputChannel(batteryLevelChannel());
        return adapter;
    }

    @Bean
    public MessageProducer intrusionInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-intrusion",
                                                                        "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/vs121/e/occupancy");
        adapter.setOutputChannel(intrusionChannel());
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
    public MessageHandler handler() {
        return message -> {
            try {
                String payload = message.getPayload().toString();
                influxService.save(message.getHeaders(), payload);
            } catch (InfluxException e) {
                log.debug("InfluxDB에 데이터 저장 과정 중 예외 발생:" + System.lineSeparator() + e);
                messageSender.send(Constants.INFLUX_DB, Constants.INFLUX_SAVE_ERROR_MESSAGE);
            }
        };
    }

    private MqttPahoMessageDrivenChannelAdapter createMqttAdapter(String url, String clientId, String... topic) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(url, clientId, topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        return adapter;
    }
}
