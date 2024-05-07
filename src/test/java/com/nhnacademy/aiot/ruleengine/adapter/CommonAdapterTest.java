package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.dto.DeviceSensorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommonAdapterTest {
    private CommonAdapter commonAdapter;

    @BeforeEach
    public void setup() {
        commonAdapter = mock(CommonAdapter.class);
    }

    @Test
    public void getDeviceSensorTest() {
        DeviceSensorResponse mockResponse = new DeviceSensorResponse();
        when(commonAdapter.getOnOffValue(any(Long.class), any(Long.class))).thenReturn(mockResponse);
        DeviceSensorResponse actualResponse = commonAdapter.getOnOffValue(1L, 1L);
        assertEquals(mockResponse, actualResponse);
    }
}