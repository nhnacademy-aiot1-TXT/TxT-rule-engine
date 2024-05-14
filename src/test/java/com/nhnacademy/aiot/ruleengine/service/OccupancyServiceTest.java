package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(classes = OccupancyService.class)
class OccupancyServiceTest {

    public static final String TEST = "test";
    @MockBean
    private RedisAdapter redisAdapter;
    @MockBean
    private CommonAdapter commonAdapter;
    private OccupancyService occupancyService;
    private Payload payload;

    @BeforeEach
    void setUp() {
        payload = new Payload(123L, Constants.OCCUPIED);
        occupancyService = new OccupancyService(redisAdapter, commonAdapter);
    }


    @Test
    void shouldStartProcess() {
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY + Constants.STATUS, TEST)).thenReturn(Constants.VACANT);

        boolean result = occupancyService.shouldStartProcess(payload, TEST);

        assertTrue(result);
    }

    @Test
    void setTimer() {
        when(redisAdapter.hasKey(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER)).thenReturn(false);
        doNothing().when(redisAdapter)
                   .setValue(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER, payload.getTime());

        Payload result = occupancyService.setTimer(payload, TEST);

        assertEquals(payload, result);
        verify(redisAdapter).hasKey(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER);
        verify(redisAdapter).setValue(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER, payload.getTime());

    }

    @Test
    void getTimer() {
        when(redisAdapter.getLongValue(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER)).thenReturn(1234L);
        Long result = occupancyService.getTimer(TEST);

        assertEquals(1234L, result);
        verify(redisAdapter).getLongValue(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER);
    }

    @Test
    void isTimerActive() {
        when(redisAdapter.getLongValue(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER)).thenReturn(12L);
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY + Constants.STATUS, TEST)).thenReturn(Constants.VACANT);

        boolean result = occupancyService.isTimerActive(payload, TEST);

        assertTrue(result);
    }

    @Test
    void save() {
        doNothing().when(redisAdapter).saveStringToList(TEST + Constants.OCCUPANCY_LABEL, payload.getValue());

        occupancyService.save(payload, TEST);

        verify(redisAdapter).saveStringToList(TEST + Constants.OCCUPANCY_LABEL, payload.getValue());
    }

    @Test
    void updateOccupancy() {
        when(redisAdapter.getAllStringList(TEST + Constants.OCCUPANCY_LABEL)).thenReturn(List.of(Constants.VACANT, Constants.VACANT, Constants.OCCUPIED));
        doNothing().when(redisAdapter)
                   .setValueToHash(eq(Constants.OCCUPANCY + Constants.STATUS), anyString(), anyString());
        doNothing().when(redisAdapter).delete(TEST + Constants.OCCUPANCY_LABEL);
        doNothing().when(redisAdapter).delete(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER);

        occupancyService.updateOccupancy(payload, TEST);

        verify(redisAdapter).getAllStringList(TEST + Constants.OCCUPANCY_LABEL);
        verify(redisAdapter).setValueToHash(Constants.OCCUPANCY + Constants.STATUS, TEST, Constants.VACANT);
        verify(redisAdapter).delete(TEST + Constants.OCCUPANCY_LABEL);
        verify(redisAdapter).delete(TEST + Constants.OCCUPANCY_LABEL + Constants.TIMER);
    }

    @Test
    void getOccupancyStatus() {
        when(redisAdapter.getStringFromHash(Constants.OCCUPANCY + Constants.STATUS, TEST)).thenReturn(Constants.VACANT);

        String result = occupancyService.getOccupancyStatus(TEST);

        assertEquals(Constants.VACANT, result);
    }
}
