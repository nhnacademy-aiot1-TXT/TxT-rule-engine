package com.nhnacademy.aiot.ruleengine.domain;

import lombok.*;

/**
 * 센서 측정치를 표현하는 클래스입니다.
 * 이 클래스는 시간, 장치이름, 센서 설치 장소, 토픽, 값, influxDB의 Measurement 정보를 필드로 가지고 있습니다.
 *
 * @author jjunho50
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorMeasurement {
    private Long time;
    private String device;
    private String place;
    private String topic;
    private String value;
    private String measurement;
}