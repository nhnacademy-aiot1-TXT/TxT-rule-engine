package com.nhnacademy.aiot.ruleengine.service;

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

    private final PointFactory pointFactory;

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String org;

    @Value("${influxdb.bucket}")
    private String bucket;

    public void saveData(String topic, String payloadStr) {
        try {
            SensorMeasurement sensorMeasurement = parseSensorMeasurement(topic, payloadStr);

            InfluxdbPoint influxdbPoint = pointFactory.getInfluxdbPoint(sensorMeasurement.getMeasurement());
            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

            Point point = influxdbPoint.build(sensorMeasurement);
            writeApi.writePoint(point);
            influxDBClient.close();
        } catch (MeasurementParseException e) {
            //
        }
    }

    private SensorMeasurement parseSensorMeasurement(String topic, String payloadStr) {
        String[] topics = topic.split("/");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Payload payload = createPayload(topic, payloadStr, objectMapper);

        return SensorMeasurement.builder()
                .time(payload.getTime())
                .device(topics[8])
                .place(topics[6])
                .topic(topic)
                .value(payload.getValue())
                .measurement(topics[10])
                .build();
    }

    private Payload createPayload(String topic, String payloadStr, ObjectMapper objectMapper) {
        if (topic.contains("people_counter")) {
            return getCountPayload(payloadStr, objectMapper);
        }
        try {
            return objectMapper.readValue(payloadStr, Payload.class);
        } catch (IOException e) {
            throw new MeasurementParseException("payload 불러오기 실패");
        }
    }

    private Payload getCountPayload(String payloadStr, ObjectMapper objectMapper) {
        if (!payloadStr.contains("total_data")) {
            throw new MeasurementParseException("payload 불러오기 실패");
        }

        try {
            JsonNode root = objectMapper.readTree(payloadStr);
            long time = convertToUnixTimestamp(root.get("time_info").get("report_time").asText());

            String value = root.get("total_data").get(0).get("capacity_counted").asText();
            return new Payload(time, value);
        } catch (IOException e) {
            throw new MeasurementParseException("payload 불러오기 실패");
        }
    }

    public static long convertToUnixTimestamp(String isoDate) {
        ZonedDateTime dateTime = ZonedDateTime.parse(isoDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return dateTime.toInstant().toEpochMilli();
    }
}