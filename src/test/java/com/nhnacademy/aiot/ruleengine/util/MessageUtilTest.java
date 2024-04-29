package com.nhnacademy.aiot.ruleengine.util;

import com.nhnacademy.aiot.ruleengine.dto.message.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageUtilTest {
    @Test
    void getMessage() {
        String topic = "data/s/nhnacademy/b/gyeongnam/p/storage/d/24e124136d151547/e/temperature";
        String payload = "{\"time\":1714117279580,\"value\":24.1}";

        Message message = MessageUtil.getMessage(topic, payload);

        assertNotNull(message);
        assertEquals("24.1", message.getValue());
    }
}