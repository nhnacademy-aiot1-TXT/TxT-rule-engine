package com.nhnacademy.aiot.ruleengine.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ConstantConditions")
public class OccupancyRedisService {

    public static final String OCCUPANCY_TIMER = "occupancy_timer";
    public static final String OCCUPANCY_STATUS = "occupancy_status";
    public static final String OCCUPANCY = "occupancy";
    public static final String OCCUPIED = "occupied";
    public static final String VACANT = "vacant";

    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public boolean hasOccupancyTimer() {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(OCCUPANCY_TIMER));
    }

    public void setOccupancyTimer(Long value) {
        longRedisTemplate.opsForValue().set(OCCUPANCY_TIMER, value);
    }

    public Long getOccupancyTimer() {
        return longRedisTemplate.opsForValue().get(OCCUPANCY_TIMER);
    }

    public String getOccupancyStatus() {
        return String.valueOf(stringRedisTemplate.opsForValue().get(OCCUPANCY_STATUS));
    }


    public void saveList(String value) {
        stringRedisTemplate.opsForList().rightPush(OCCUPANCY, value);
    }

    public void setOccupancyStatus() {
        stringRedisTemplate.opsForValue().set(OCCUPANCY_STATUS, isOcucpied());
        stringRedisTemplate.delete(OCCUPANCY);
        longRedisTemplate.delete(OCCUPANCY_TIMER);
    }

    private String isOcucpied() {
        List<String> list = stringRedisTemplate.opsForList().range(OCCUPANCY, 0, -1);
        return Collections.frequency(list, OCCUPIED) >= Collections.frequency(list, VACANT) ?
                OCCUPIED : VACANT;
    }
}
