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

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfluxServiceTest {

    @InjectMocks
    private InfluxService influxService;
    @Mock
    private ObjectMapper objectMapper;
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
        Field url = InfluxService.class.getDeclaredField("url");
        url.setAccessible(true);
        url.set(influxService,"http://0.0.0.0:0000");
        Field token = InfluxService.class.getDeclaredField("token");
        token.setAccessible(true);
        token.set(influxService, "test-token");
        Field org = InfluxService.class.getDeclaredField("org");
        org.setAccessible(true);
        org.set(influxService,"test-org");
        Field bucket = InfluxService.class.getDeclaredField("bucket");
        bucket.setAccessible(true);
        bucket.set(influxService, "test-bucket");

        when(pointFactory.getInfluxdbPoint(anyString())).thenReturn(influxdbPoint);
        when(influxDBClient.getWriteApiBlocking()).thenReturn(writeApiBlocking);
        when(influxdbPoint.build(any())).thenReturn(mock(Point.class));
    }

    @Test
    void saveData() throws JsonProcessingException {
        String topic = "milesight/s/nhnacademy/b/gyeongnam/p/pair_room/d/vs330/e/occupancy";
        String payloadStr = "{\"time\":1712910393668,\"value\":88}";
        Payload payload = new Payload(1712910393668L, "88");
        MockedStatic<InfluxDBClientFactory> influxDBClientFactoryMockedStatic = mockStatic(InfluxDBClientFactory.class);
        influxDBClientFactoryMockedStatic.when(() -> InfluxDBClientFactory.create(anyString(), any(char[].class), anyString(), anyString())
                                                                          .close()
        ).thenReturn(influxDBClient);
        when(objectMapper.readValue(payloadStr, Payload.class)).thenReturn(payload);

        influxService.saveData(topic, payloadStr);

        verify(objectMapper).readValue(payloadStr, Payload.class);
        verify(pointFactory).getInfluxdbPoint("occupancy");
        verify(writeApiBlocking).writePoint(any(Point.class));
        influxDBClientFactoryMockedStatic.close();
    }
}
