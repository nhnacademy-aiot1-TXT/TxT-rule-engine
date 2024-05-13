package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.TimeIntervalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class IntrusionService {

    private final CommonAdapter commonAdapter;

    public boolean isAlertTimeActive(LocalTime localTime) {
        TimeIntervalResponse response = commonAdapter.getTimeIntervalBySensorName(Constants.OCCUPANCY);
        LocalTime start = response.getBegin();
        LocalTime end = response.getEnd();


        // e.g. 0시 ~ 6시
        if (start.isBefore(end)) {
            return (start.isBefore(localTime) || start.equals(localTime)) && localTime.isBefore(end);
        }

        // e.g. 22시 ~ 6시
        if (start.isAfter(end)) {
            return (start.isBefore(localTime) || start.equals(localTime)) || localTime.isBefore(end);
        }

        // e.g. 22시 ~ 22시
        return start.equals(localTime);
    }
}
