package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;

import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloatValuePointTest {

    private FloatValuePoint floatValuePoint;

    @BeforeEach
    void setUp() {
        floatValuePoint = new FloatValuePoint();
    }

    @Test
    void build() {
        SensorMeasurement sensorMeasurement = SensorMeasurement.builder()
                                                               .time(123L)
                                                               .device("test")
                                                               .place("test")
                                                               .topic("test")
                                                               .value("23.0000000004")
                                                               .measurement("test")
                                                               .build();

        Point point = floatValuePoint.build(sensorMeasurement);

        assertTrue(point.toLineProtocol().contains("value=23.0"));
    }
}
