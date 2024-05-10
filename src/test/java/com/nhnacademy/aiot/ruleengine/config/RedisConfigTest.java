package com.nhnacademy.aiot.ruleengine.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @InjectMocks
    private RedisConfig redisConfig;

    @Test
    void longRedisTemplate() {
        RedisTemplate<String, Long> redisTemplate = redisConfig.longRedisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate);
    }

    @Test
    void floatRedisTemplate() {
        RedisTemplate<String, Float> redisTemplate = redisConfig.floatRedisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate);
    }

    @Test
    void strRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = redisConfig.strRedisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate);
    }

    @Test
    void doubleRedisTemplate() {
        RedisTemplate<String, Double> redisTemplate = redisConfig.doubleRedisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate);
    }
}