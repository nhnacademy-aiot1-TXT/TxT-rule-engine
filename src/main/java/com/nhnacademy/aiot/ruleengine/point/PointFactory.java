package com.nhnacademy.aiot.ruleengine.point;

import org.springframework.stereotype.Component;

@Component
public class PointFactory {
    public InfluxdbPoint getInfluxdbPoint(String measurement) {
        if ("occupancy".equals(measurement)) {
            return new StringValuePoint();
        }
        return new FloatValuePoint();
    }
}
