package com.nhnacademy.aiot.ruleengine;

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

    @Override
    public void run(String... args) throws Exception {
        Collection<Object> ruleInfos = redisAdapter.getEntries("rule_infos").values();
        for (Object ruleInfo : ruleInfos) {
            ruleService.createRule((RuleInfo) ruleInfo);
        }
    }
}

