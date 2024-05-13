package com.nhnacademy.aiot.ruleengine.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageHeaders;
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

    public void save(MessageHeaders headers, String payloadStr) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

        SensorData sensorData = sensorService.build(headers, payloadStr);
        Point point = addValueToInfluxPoint(sensorData.getValue(),
                                            Point.measurement(sensorData.getMeasurement())
                                                 .addField(Constants.TIME, sensorData.getTime())
                                                 .addField(Constants.DEVICE, sensorData.getDevice())
                                                 .addField(Constants.PLACE, sensorData.getPlace())
                                                 .addField(Constants.TOPIC, sensorData.getTopic()));
        writeApi.writePoint(point);
        influxDBClient.close();
    }

    private boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Point addValueToInfluxPoint(String value, Point point) {
        if (isFloat(value)) {
            return point.addField(Constants.VALUE, sensorService.parseToFloatValue(value));
        }
        return point.addField(Constants.VALUE, value);
    }
}
