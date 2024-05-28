package com.nhnacademy.aiot.ruleengine.service;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(OccupancyService.class)
class OccupancyServiceTest {

    @MockBean
    private InfluxService influxService;
    @MockBean
    private RedisAdapter redisAdapter;
    @Autowired
    private OccupancyService occupancyService;

    @Test
    void getOccupancyStatus() {
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY, "test")).thenReturn(Constants.OCCUPIED);

        String result = occupancyService.getOccupancyStatus("test");

        assertEquals(Constants.OCCUPIED, result);
    }

    @Test
    void updateOccupiedToVacant() {
        FluxTable fluxTable = mock(FluxTable.class);
        FluxRecord fluxRecord = mock(FluxRecord.class);
        when(influxService.query(anyString())).thenReturn(List.of(fluxTable));
        when(fluxRecord.getRow()).thenReturn(List.of("result", "1", Constants.CLASS_A, Constants.VACANT));
        when(fluxTable.getRecords()).thenReturn(List.of(fluxRecord, fluxRecord));
        doNothing().when(redisAdapter).setValueToHash(anyString(), anyString(), anyString());
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY, Constants.CLASS_A)).thenReturn(Constants.OCCUPIED);

        occupancyService.updateAll();

        verify(redisAdapter).setValueToHash(Constants.OCCUPANCY, Constants.CLASS_A, Constants.VACANT);
    }

    @Test
    void updateVacantToOccupied() {
        FluxTable fluxTable = mock(FluxTable.class);
        FluxRecord fluxRecordOccupied = mock(FluxRecord.class);
        FluxRecord fluxRecordVacant = mock(FluxRecord.class);
        when(influxService.query(anyString())).thenReturn(List.of(fluxTable));
        when(fluxRecordOccupied.getRow()).thenReturn(List.of("result", "1", Constants.CLASS_A, Constants.OCCUPIED));
        when(fluxRecordVacant.getRow()).thenReturn(List.of("result", "1", Constants.CLASS_A, Constants.VACANT));
        when(fluxTable.getRecords()).thenReturn(List.of(fluxRecordOccupied, fluxRecordOccupied, fluxRecordVacant));
        doNothing().when(redisAdapter).setValueToHash(anyString(), anyString(), anyString());
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY, Constants.CLASS_A)).thenReturn(Constants.VACANT);

        occupancyService.updateAll();

        verify(redisAdapter).setValueToHash(Constants.OCCUPANCY, Constants.CLASS_A, Constants.OCCUPIED);
    }
}
