package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.sensor.StringValueSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InfluxServiceTest {
    @InjectMocks
    private InfluxService influxService;
    @Mock
    private SensorService sensorService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private InfluxDBClient influxDBClient;
    @Mock
    private WriteApiBlocking writeApiBlocking;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(influxService, "url", "http://localhost:8086");
        ReflectionTestUtils.setField(influxService, "token", "XXEa9ArQqioW7NKxwjQnuHDBdLOPRAsKCsifTtaDaavtDpkJKiSJ9ZUkwwuJEahui1QpCfzUJ0ZRkJrduobhBg==");
        ReflectionTestUtils.setField(influxService, "org", "nhnacademy");
        ReflectionTestUtils.setField(influxService, "bucket", "TxT-iot");
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApiBlocking);
    }

    @Test
    public void testSave() throws JsonProcessingException {
        String topic = "milesight/s/nhnacademy/b/gyeongnam/p/class_a/d/vs121/e/occupancy";
        String payload = "{\"time\":1713406102466,\"value\":\"occupied\"}";

        StringValueSensor mockSensorData = StringValueSensor.builder()
                .time(1713406102466L)
                .place("class_a")
                .topic("occupied")
                .device("vs121")
                .measurement("occupancy")
                .value("occupied")
                .build();

        Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(Payload.class)))
                .thenReturn(new Payload(1713406102466L, "occupied"));

        when(sensorService.build(topic, payload)).thenReturn(mockSensorData);
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApiBlocking);

        influxService.save(topic, payload);
        verify(sensorService).build(topic, payload);
    }
}