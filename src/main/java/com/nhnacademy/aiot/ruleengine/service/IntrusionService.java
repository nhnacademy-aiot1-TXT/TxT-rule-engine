package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntrusionService {

    private final RedisAdapter redisAdapter;

    private int getHours(Long time) {
        int i = (int) ((time / (1000 * 60 * 60)) % 24) + 9;
        return i > 24 ? i - 24 : i;
    }

    public boolean isAlertTimeActive(Long time) {
        int hours = getHours(time);
        int start = redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.START);
        int end = redisAdapter.getIntFromHash(Constants.INTRUSION_TIME, Constants.END);

        // e.g., 0시 ~ 6시
        if (start < end) {
            return start <= hours && hours < end;
        }

        // e.g., 22시 ~ 6시
        if (start > end) {
            return start <= hours || hours < end;
        }

        // e.g., 22시 ~ 22시
        return start == hours;
    }

}
