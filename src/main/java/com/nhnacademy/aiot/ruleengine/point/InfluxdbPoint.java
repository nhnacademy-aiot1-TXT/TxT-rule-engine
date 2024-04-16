package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;

public interface InfluxdbPoint {
    default Point build(SensorMeasurement sensorMeasurement) {
        return Point.measurement(sensorMeasurement.getMeasurement())
                    .addField("time", sensorMeasurement.getTime())
                    .addField("device", sensorMeasurement.getDevice())
                    .addField("place", sensorMeasurement.getPlace())
                    .addField("topic", sensorMeasurement.getTopic());
    }
}
