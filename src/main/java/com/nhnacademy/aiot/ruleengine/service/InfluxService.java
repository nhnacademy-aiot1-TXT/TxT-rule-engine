package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SensorMeasurement;
import com.nhnacademy.aiot.ruleengine.exception.MeasurementParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfluxService {

    private final ObjectMapper objectMapper;

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String org;

    @Value("${influxdb.bucket}")
    private String bucket;

    private SensorMeasurement parseSensorMeasurement(String topic, String payloadStr) {
        String[] topics = topic.split("/");
        Payload payload;
        try {
            payload = objectMapper.readValue(payloadStr, Payload.class);
        } catch (JsonProcessingException e) {
            throw new MeasurementParseException("payload 불러오기 실패");
        }
        return SensorMeasurement.builder()
                                .time(payload.getTime())
                                .device(topics[8])
                                .place(topics[6])
                                .topic(topic)
                                .value(payload.getValue())
                                .measurement(topics[10])
                                .build();
    }

    public void saveData(String topic, String payloadStr) {
        SensorMeasurement sensorMeasurement = parseSensorMeasurement(topic, payloadStr);
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = Point.measurement(sensorMeasurement.getMeasurement())
                           .addField("time", sensorMeasurement.getTime())
                           .addField("device", sensorMeasurement.getDevice())
                           .addField("place", sensorMeasurement.getPlace())
                           .addField("topic", sensorMeasurement.getTopic());
        if ("battery_level".equals(sensorMeasurement.getMeasurement())) {
            point.addField("value", Integer.parseInt(sensorMeasurement.getValue()));
        } else {
            point.addField("value", sensorMeasurement.getValue());
        }
        writeApi.writePoint(point);
        influxDBClient.close();
    }
}
