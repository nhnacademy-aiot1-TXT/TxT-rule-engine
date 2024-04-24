package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 해당 device의 ON/OFF 여부 확인 클래스
 *
 * @author jjunho50
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SwitchState {
    /**
     * ON/OFF == TRUE/FALSE
     */
    private boolean state;
}
