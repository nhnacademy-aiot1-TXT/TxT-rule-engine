package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.Payload;
import com.nhnacademy.aiot.ruleengine.point.InfluxdbPoint;
import com.nhnacademy.aiot.ruleengine.point.PointFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfluxServiceTest {

    @InjectMocks
    private InfluxService influxService;
    @Mock
    private PointFactory pointFactory;
    @Mock
    private InfluxdbPoint influxdbPoint;
    @Mock
    private InfluxDBClient influxDBClient;
    @Mock
    private WriteApiBlocking writeApiBlocking;

    @BeforeEach
    void setUp() throws ReflectiveOperationException{
        ReflectionTestUtils.setField(influxService, "url", "http://133.186.251.19:8086");
        ReflectionTestUtils.setField(influxService, "token", "auLmfVaJpvWUbnMxhbixfgq5JjFrleKTNxnFphRZ_tfVHoypyyXhBe3zHT07tqRTylE15VRmjuNFX9-u9uv6nA==");
        ReflectionTestUtils.setField(influxService, "org", "TXT");
        ReflectionTestUtils.setField(influxService, "bucket", "TxT-iot");
    }

    @Test
    void saveData() throws JsonProcessingException {
        String topic = "milesight/s/nhnacademy/b/gyeongnam/p/pair_room/d/vs330/e/occupancy";
        String payloadStr = "{\"time\":1712910393668,\"value\":88}";

        when(pointFactory.getInfluxdbPoint(anyString())).thenReturn(influxdbPoint);
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApiBlocking);
        when(influxdbPoint.build(any())).thenReturn(mock(Point.class));

        try (MockedStatic<InfluxDBClientFactory> influxDBClientFactoryMockedStatic = mockStatic(InfluxDBClientFactory.class)) {
            influxDBClientFactoryMockedStatic.when(() -> InfluxDBClientFactory.create(anyString(), any(char[].class), anyString(), anyString())).thenReturn(influxDBClient);

            influxService.saveData(topic, payloadStr);

            verify(pointFactory).getInfluxdbPoint("occupancy");
            verify(influxDBClient).getWriteApiBlocking();
            verify(writeApiBlocking).writePoint(any(Point.class));
            verify(influxDBClient).close();
        }
    }
}
