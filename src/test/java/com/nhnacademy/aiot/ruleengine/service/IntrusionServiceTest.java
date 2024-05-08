package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@SpringJUnitConfig(classes = IntrusionService.class)
class IntrusionServiceTest {

    @Autowired
    private IntrusionService intrusionService;
    @MockBean
    private RedisAdapter redisAdapter;

    private Long time1400;
    private Long time0200;
    private Long time2200;
    private Long time2330;

    @BeforeEach
    void setUp() {
        time0200 = 1715014800000L;
        time1400 = 1715058000000L;
        time2200 = 1715086800000L;
        time2330 = 1715092200000L;
    }

    @Test
    void test0To6() {
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.START)).thenReturn(0);
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.END)).thenReturn(6);

        assertTrue(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertFalse(intrusionService.isAlertTimeActive(time2200));
        assertFalse(intrusionService.isAlertTimeActive(time2330));
    }

    @Test
    void test22To6() {
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.START)).thenReturn(22);
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.END)).thenReturn(6);

        assertTrue(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertTrue(intrusionService.isAlertTimeActive(time2200));
        assertTrue(intrusionService.isAlertTimeActive(time2330));
    }

    @Test
    void test22To22() {
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.START)).thenReturn(22);
        when(redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.END)).thenReturn(22);

        assertFalse(intrusionService.isAlertTimeActive(time0200));
        assertFalse(intrusionService.isAlertTimeActive(time1400));
        assertTrue(intrusionService.isAlertTimeActive(time2200));
        assertFalse(intrusionService.isAlertTimeActive(time2330));
    }
}
