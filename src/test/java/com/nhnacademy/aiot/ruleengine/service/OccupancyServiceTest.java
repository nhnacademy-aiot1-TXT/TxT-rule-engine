package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OccupancyServiceTest {

    @MockBean
    private RedisAdapter redisAdapter;

    @Test
    public void testHasTimer() {
        Mockito.when(redisAdapter.hasTimer(Constants.OCCUPANCY)).thenReturn(true);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        boolean result = occupancyService.hasTimer();

        assertThat(result).isEqualTo(true);
    }

    @Test
    public void testSetTimer() {
        Mockito.when(redisAdapter.hasTimer(Constants.OCCUPANCY)).thenReturn(false);
        Mockito.when(redisAdapter.getStatus(Constants.OCCUPANCY)).thenReturn(Constants.VACANT);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        Payload payload = new Payload(1713406102466L, Constants.OCCUPIED);
        Payload result = occupancyService.setTimer(payload);

        assertThat(result).isEqualTo(payload);
        Mockito.verify(redisAdapter, Mockito.times(1)).setTimer(Constants.OCCUPANCY, payload.getTime());
    }

    @Test
    public void testGetTimer() {
        Mockito.when(redisAdapter.getTimer(Constants.OCCUPANCY)).thenReturn(1713406102466L);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        Long result = occupancyService.getTimer();

        assertThat(result).isEqualTo(1713406102466L);
    }

    @Test
    public void testGetOccupancyStatus() {
        Mockito.when(redisAdapter.getStatus(Constants.OCCUPANCY)).thenReturn(Constants.OCCUPIED);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        String result = occupancyService.getOccupancyStatus();

        assertThat(result).isEqualTo(Constants.OCCUPIED);
    }

    @Test
    public void testSaveToList() {
        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        occupancyService.saveToList(Constants.OCCUPIED);

        Mockito.verify(redisAdapter, Mockito.times(1)).saveStringToList(Constants.OCCUPANCY, Constants.OCCUPIED);
    }

    @Test
    public void testSetOccupancyStatus() {
        List<String> list = Arrays.asList(Constants.OCCUPIED, Constants.OCCUPIED, Constants.VACANT);
        Mockito.when(redisAdapter.getAllStringList(Constants.OCCUPANCY)).thenReturn(list);
        Mockito.when(redisAdapter.getStatus(Constants.OCCUPANCY)).thenReturn(Constants.VACANT);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        occupancyService.setOccupancyStatus();

        Mockito.verify(redisAdapter, Mockito.times(1)).setStatus(Constants.OCCUPANCY, Constants.OCCUPIED);
        Mockito.verify(redisAdapter, Mockito.times(1)).delete(Constants.OCCUPANCY);
        Mockito.verify(redisAdapter, Mockito.times(1)).deleteTimer(Constants.OCCUPANCY);
    }

    @Test
    public void testUpdateOccupancy() {
        Mockito.when(redisAdapter.getTimer(Constants.OCCUPANCY)).thenReturn(1713406102466L);

        OccupancyService occupancyService = new OccupancyService(redisAdapter);

        Payload payload = new Payload(1713406102466L, Constants.OCCUPIED);
        Payload result = occupancyService.updateOccupancy(payload);

        assertThat(result).isNotNull();
        Mockito.verify(redisAdapter, Mockito.times(1)).saveStringToList(Constants.OCCUPANCY, Constants.OCCUPIED);
    }
}