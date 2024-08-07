package com.nhnacademy.aiot.ruleengine.config.flow;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.service.OccupancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.support.GenericMessage;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OccupancyFlowConfig {

    private final OccupancyService occupancyService;

    @Bean
    public IntegrationFlow occupancyProcess() {
        return IntegrationFlows.from(() -> new GenericMessage<>("trigger"),
                                     c -> c.poller(Pollers.fixedRate(Constants.TEN_MINUTES)))
                               .handle((payload, headers) ->
                                           {
                                               occupancyService.updateAll();
                                               log.info("재실 상태 업데이트 완료");
                                               return null;
                                           }).nullChannel();
    }
}
