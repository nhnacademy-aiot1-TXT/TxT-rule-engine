package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@SpringJUnitConfig(classes = IntrusionService.class)
class IntrusionServiceTest {

    @Autowired
    private IntrusionService intrusionService;
    @MockBean
    private CommonAdapter commonAdapter;

    private LocalTime time1400;
    private LocalTime time0200;
    private LocalTime time2200;
    private LocalTime time2330;

    @BeforeEach
    void setUp() {
        time0200 = LocalTime.of(2, 0);
        time1400 = LocalTime.of(14, 0);
        time2200 = LocalTime.of(22, 0);
        time2330 = LocalTime.of(23, 30);
    }

    @Test
    void test0To6() {
        when(commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY)).thenReturn(new TimeIntervalResponse(7L, 1L, Constants.OCCUPANCY, LocalTime.of(0, 0, 0), LocalTime.of(6, 0, 0)));

        assertTrue(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertFalse(intrusionService.isAlertTimeActive(time2200));
        assertFalse(intrusionService.isAlertTimeActive(time2330));
    }

    @Test
    void test22To6() {
        when(commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY)).thenReturn(new TimeIntervalResponse(7L, 1L, Constants.OCCUPANCY, LocalTime.of(22, 0, 0), LocalTime.of(6, 0, 0)));

        assertTrue(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertTrue(intrusionService.isAlertTimeActive(time2200));
        assertTrue(intrusionService.isAlertTimeActive(time2330));
    }

    @Test
    void test22To22() {
        when(commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY)).thenReturn(new TimeIntervalResponse(7L, 1L, Constants.OCCUPANCY, LocalTime.of(22, 0, 0), LocalTime.of(22, 0, 0)));

        assertFalse(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertTrue(intrusionService.isAlertTimeActive(time2200));
        assertFalse(intrusionService.isAlertTimeActive(time2330));
    }
}
