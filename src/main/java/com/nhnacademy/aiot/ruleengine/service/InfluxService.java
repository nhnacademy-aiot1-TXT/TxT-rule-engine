package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.Payload;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;
import com.nhnacademy.aiot.ruleengine.exception.MeasurementParseException;
import com.nhnacademy.aiot.ruleengine.point.InfluxdbPoint;
import com.nhnacademy.aiot.ruleengine.point.PointFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InfluxService {

    private final ObjectMapper objectMapper;

    private final PointFactory pointFactory;

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

    private SensorMeasurement totalCountSensorMeasurement(String topic, String payloadStr)
    {
        String[] topics = topic.split("/");
        Payload payload;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            JsonNode root = objectMapper.readTree(payloadStr);
            long time = convertToUnixTimestamp(root.get("time_info").get("report_time").asText());
            String value = root.get("total_data").get(0).get("capacity_counted").asText();

            payload = new Payload(time, value);
        } catch (IOException e) {
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
        SensorMeasurement sensorMeasurement = topic.contains("people_counter") ? totalCountSensorMeasurement(topic, payloadStr) : parseSensorMeasurement(topic, payloadStr);
        InfluxdbPoint influxdbPoint = pointFactory.getInfluxdbPoint(sensorMeasurement.getMeasurement());
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        Point point = influxdbPoint.build(sensorMeasurement);
        writeApi.writePoint(point);
        influxDBClient.close();
    }

    public static long convertToUnixTimestamp(String isoDate) {
        ZonedDateTime dateTime = ZonedDateTime.parse(isoDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return dateTime.toInstant().toEpochMilli();
    }
}
