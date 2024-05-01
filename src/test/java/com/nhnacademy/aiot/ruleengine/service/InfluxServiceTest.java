package com.nhnacademy.aiot.ruleengine.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.nhnacademy.aiot.ruleengine.dto.SensorData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
public class InfluxServiceTest {

    @MockBean
    private InfluxDBClientFactory influxDBClientFactory;

    @MockBean
    private SensorService sensorService;
    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String org;

    @Value("${influxdb.bucket}")
    private String bucket;

    @Test
    public void testSave() {
        InfluxService influxService = new InfluxService(sensorService);
        InfluxDBClient influxDBClient = Mockito.mock(InfluxDBClient.class);
        WriteApiBlocking writeApi = Mockito.mock(WriteApiBlocking.class);
        SensorData sensorData = mockSensorData();

        Mockito.when(influxDBClient.toString()).thenReturn("Mocked InfluxDBClient");
        Mockito.when(influxDBClientFactory.create(url, token.toCharArray(), org, bucket))
                .thenReturn(influxDBClient);
        Mockito.when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApi);
        Mockito.when(sensorService.build(Mockito.any(), Mockito.any())).thenReturn(sensorData);

        influxService.save(
                MessageBuilder.withPayload("dummyPayload")
                        .setHeader("mqtt_receivedTopic", "23.6")
                        .build()
                        .getHeaders(),
                "dummyPayloadString"
        );

        Mockito.verify(writeApi, Mockito.times(1)).writePoint(Mockito.any());
        Mockito.verify(influxDBClient, Mockito.times(1)).close();
    }

    private SensorData mockSensorData() {
        return SensorData.builder()
                .time(1571885523L)
                .device("device")
                .place("place")
                .topic("topic")
                .measurement("measurement")
                .value("23.6")
                .build();
    }
}