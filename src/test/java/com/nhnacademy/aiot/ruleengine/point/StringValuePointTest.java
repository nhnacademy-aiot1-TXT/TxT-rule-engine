package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringValuePointTest {

    private StringValuePoint stringValuePoint;

    @BeforeEach
    void setUp() {
        stringValuePoint = new StringValuePoint();
    }

    @Test
    void build() {
        SensorMeasurement sensorMeasurement = SensorMeasurement.builder()
                                                               .time(123L)
                                                               .device("test")
                                                               .place("test")
                                                               .topic("test")
                                                               .value("occupied")
                                                               .measurement("test")
                                                               .build();

        Point point = stringValuePoint.build(sensorMeasurement);

        assertTrue(point.toLineProtocol().contains("value=\"occupied\""));
    }
}
