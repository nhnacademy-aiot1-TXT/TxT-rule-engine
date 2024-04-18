package com.nhnacademy.aiot.ruleengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 해당 device의 ON/OFF 여부 확인 클래스
 *
 * @author jjunho50
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SwitchState {
    /**
     * ON/OFF == TRUE/FALSE
     */
    private boolean state;
}
