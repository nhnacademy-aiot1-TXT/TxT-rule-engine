package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfluxdbPointTest {

    private InfluxdbPoint influxdbPoint;

    @BeforeEach
    void setUp() {
        influxdbPoint = new InfluxdbPoint() {
            @Override
            public Point build(SensorMeasurement sensorMeasurement) {
                return InfluxdbPoint.super.build(sensorMeasurement);
            }
        };
    }

    @Test
    void build() {
        SensorMeasurement sensorMeasurement = SensorMeasurement.builder()
                                                               .time(1234L)
                                                               .device("test-device")
                                                               .place("test-place")
                                                               .topic("test-topic")
                                                               .measurement("test-measurement")
                                                               .build();

        Point point = influxdbPoint.build(sensorMeasurement);

        String lineProtocol = point.toLineProtocol();

        assertTrue(lineProtocol.contains("test-measurement"));
        assertTrue(lineProtocol.contains("time=1234"));
        assertTrue(lineProtocol.contains("device=\"test-device\""));
        assertTrue(lineProtocol.contains("place=\"test-place\""));
        assertTrue(lineProtocol.contains("topic=\"test-topic\""));
    }
}
