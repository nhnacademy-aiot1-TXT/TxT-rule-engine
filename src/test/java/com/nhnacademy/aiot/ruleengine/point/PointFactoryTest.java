package com.nhnacademy.aiot.ruleengine.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointFactoryTest {

    private PointFactory pointFactory;

    @BeforeEach
    void setUp() {
        pointFactory = new PointFactory();
    }

    @Test
    void getStringValuePoint() {
        InfluxdbPoint influxdbPoint = pointFactory.getInfluxdbPoint("magnet_status");

        assertEquals(StringValuePoint.class, influxdbPoint.getClass());
    }

    @Test
    void getFloatValuePoint() {
        InfluxdbPoint influxdbPoint = pointFactory.getInfluxdbPoint("temperature");

        assertEquals(FloatValuePoint.class,influxdbPoint.getClass());
    }
}
