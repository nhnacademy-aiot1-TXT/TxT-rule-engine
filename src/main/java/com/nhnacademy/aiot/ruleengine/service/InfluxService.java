package com.nhnacademy.aiot.ruleengine.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.dto.sensor.BaseSensor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfluxService {

    private final SensorService sensorService;

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String org;

    @Value("${influxdb.bucket}")
    private String bucket;

    public void save(String topic, String payloadStr) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        BaseSensor sensorData = sensorService.build(topic, payloadStr);
        Point point = sensorData.addValueToInfluxPoint(
                Point.measurement(sensorData.getMeasurement())
                     .addField("time", sensorData.getTime())
                     .addField("device", sensorData.getDevice())
                     .addField("place", sensorData.getPlace())
                     .addField("topic", sensorData.getTopic()));
        writeApi.writePoint(point);
        influxDBClient.close();
    }
}
