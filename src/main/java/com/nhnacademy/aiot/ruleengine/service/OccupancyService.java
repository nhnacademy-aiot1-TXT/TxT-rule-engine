package com.nhnacademy.aiot.ruleengine.service;

import com.influxdb.query.FluxTable;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OccupancyService {

    private final InfluxService influxService;
    private final RedisAdapter redisAdapter;

    public String getOccupancyStatus(String place) {
        return redisAdapter.getStringFromHash(Constants.OCCUPANCY, place);
    }

    public void updateAll() {
        List<FluxTable> fluxTables = getValuesByPlace();
        for (FluxTable table : fluxTables) {
            List<String> collect = table.getRecords().stream().map(fluxRecord -> (String) fluxRecord.getRow().get(3)).collect(Collectors.toList());
            upadate(Constants.VACANT, table, isOccupied(collect));
            upadate(Constants.OCCUPIED, table, isVacant(collect));
        }
    }

    private List<FluxTable> getValuesByPlace() {
        String query = "from(bucket: \"TxT-iot\")" +
                "  |> range(start: -10m, stop: now())" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"occupancy\")" +
                "  |> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")" +
                "  |> group(columns: [\"place\"])" +
                "  |> drop(columns: [\"_start\", \"_stop\", \"_time\", \"_measurement\", \"device\", \"topic\"])";

        return influxService.query(query);
    }

    private void save(String place, String value) {
        redisAdapter.setValueToHash(Constants.OCCUPANCY, place, value);
    }

    private void upadate(String nowStatus, FluxTable table, boolean condition) {
        String value = Constants.OCCUPIED.equals(nowStatus) ? Constants.VACANT : Constants.OCCUPIED;
        String place = (String) table.getRecords().get(0).getRow().get(2);
        if (nowStatus.equals(getOccupancyStatus(place)) && condition) {
            save(place, value);
        }
    }

    private boolean isOccupied(List<String> values) {
        return Collections.frequency(values, Constants.OCCUPIED) >= Collections.frequency(values, Constants.VACANT);
    }

    private boolean isVacant(List<String> values) {
        return values.stream().noneMatch(Constants.OCCUPIED::equals);
    }
}

