package com.nhnacademy.aiot.ruleengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.dto.rule.RuleInfo;
import com.nhnacademy.aiot.ruleengine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class StartUpRunner implements CommandLineRunner {

    private final RuleService ruleService;
    private final RedisAdapter redisAdapter;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws JsonProcessingException {
        Collection<Object> ruleInfos = redisAdapter.getEntries("rule_infos").values();
        for (Object ruleInfoStr : ruleInfos) {
            RuleInfo ruleInfo = objectMapper.readValue((String) ruleInfoStr, RuleInfo.class);
            ruleService.createRule(ruleInfo);
        }
    }
}

