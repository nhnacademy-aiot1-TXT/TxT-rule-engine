package com.nhnacademy.aiot.ruleengine.config.flow;

import com.influxdb.exceptions.InfluxException;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.send.MessageSender;
import com.nhnacademy.aiot.ruleengine.service.InfluxService;
import com.nhnacademy.aiot.ruleengine.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
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
public class InfluxDbFlowConfig {

    private final InfluxService influxService;
    private final MessageSender messageSender;
    private final MqttService mqttService;

    @Bean
    public MessageChannel influxInputChannel() {
        return new DirectChannel();
    }

    /**
     * 이 메소드는 TxT 팀이 별도로 설치한 센서 메시지를 수신하는 데 필요한 설정을 정의하며,
     * 수신된 메시지는 txtSensorInputChannel을 통해 전달됩니다.
     *
     * @return MessageProducer 객체
     */
//    @Bean
//    public MessageProducer txtSensorInbound() {
//        return mqttService.createMqttAdapter(Constants.TXT_MQTT, "rule-engine-txt", influxInputChannel(),
//                                             "milesight/s/nhnacademy/b/gyeongnam/p/+/d/+/e/+");
//    }

    /**
     * 이 메소드는 학원의 기존 센서들의 메시지를 수신하는 데 필요한 설정을 정의
     *
     * @return MessageProducer 객체
     */
    @Bean
    public MessageProducer academySensorInbound() {

        return mqttService.createMqttAdapter(Constants.ACADEMY_MQTT, "rule-engine-academy", influxInputChannel(),
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/co2",
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/tvoc",
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/humidity",
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/temperature",
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/illumination",
                                             "data/s/nhnacademy/b/gyeongnam/p/+/d/+/e/battery_level");
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
        return message ->
            {
                try {
                    String payload = message.getPayload().toString();
                    influxService.save(message.getHeaders(), payload);
                } catch (InfluxException e) {
                    log.debug("InfluxDB에 데이터 저장 과정 중 예외 발생:" + System.lineSeparator() + e);
//                    messageSender.send(Constants.INFLUX_DB, Constants.INFLUX_SAVE_ERROR_MESSAGE);
                }
            };
    }
}
