package com.nhnacademy.aiot.ruleengine.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload를 표현하는 클래스입니다.
 * 이 클래스는 Payload의 시간과 값을 필드로 가지고 있습니다.
 *
 * @author jjunho50
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payload {
    private Long time;
    private String value;
}