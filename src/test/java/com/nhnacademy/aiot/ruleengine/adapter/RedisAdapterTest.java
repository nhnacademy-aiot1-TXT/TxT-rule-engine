package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisAdapterTest {
    @Mock
    private SensorService sensorService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOps;
    @Mock
    private ListOperations<String, Object> listOps;
    @Mock
    private HashOperations<String, Object, Object> hashOps;
    private RedisAdapter redisAdapter;
    private final String testKey = "testKey";
    private final String testHashKey = "testHashKey";
    private final String testValue = "testValue";
    private final Long testLong = 123L;

    @BeforeEach
    void setup() {
        redisAdapter = new RedisAdapter(sensorService, redisTemplate);
    }

    @Test
    void hasKey() {
        when(redisTemplate.hasKey(testKey)).thenReturn(true);

        boolean result = redisAdapter.hasKey(testKey);

        assertTrue(result);
    }

    @Test
    void hasKeyHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.hasKey(testKey, testHashKey)).thenReturn(true);

        boolean result = redisAdapter.hasKey(testKey, testHashKey);

        assertTrue(result);
    }

    @Test
    void setValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(testKey, testValue);

        redisAdapter.setValue(testKey, testValue);

        verify(valueOps).set(testKey, testValue);
    }

    @Test
    void testSetValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(testKey, testLong);

        redisAdapter.setValue(testKey, testLong);

        verify(valueOps).set(testKey, testLong);
    }

    @Test
    void getLongValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(testKey)).thenReturn(testLong);

        Long result = redisAdapter.getLongValue(testKey);

        assertEquals(testLong, result);
    }

    @Test
    void getStringValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(testKey)).thenReturn(testValue);

        String result = redisAdapter.getStringValue(testKey);

        assertEquals(testValue, result);
    }

    @Test
    void getBooleanValue() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(testKey)).thenReturn("true");

        boolean result = redisAdapter.getBooleanValue(testKey);

        assertTrue(result);
    }

    @Test
    void saveFloatToList() {
        when(sensorService.parseToFloatValue(testValue)).thenReturn(123f);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.rightPush(testKey, 123f)).thenReturn(testLong);

        redisAdapter.saveFloatToList(testKey, testValue);

        verify(sensorService).parseToFloatValue(testValue);
        verify(listOps).rightPush(testKey, 123.0f);
    }

    @Test
    void saveStringToList() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.rightPush(testKey, testValue)).thenReturn(testLong);

        redisAdapter.saveStringToList(testKey, testValue);

        verify(listOps).rightPush(testKey, testValue);
    }

    @Test
    void saveLongToList() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.rightPush(testKey, testLong)).thenReturn(testLong);

        redisAdapter.saveLongToList(testKey, testLong);

        verify(listOps).rightPush(testKey, testLong);
    }

    @Test
    void getAllDoubleList() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range((testKey), 0, -1)).thenReturn(List.of(18.0, 25.0, 23.2));

        List<Double> list = redisAdapter.getAllDoubleList(testKey);

        assertEquals(3, list.size());
    }

    @Test
    void getAllStringList() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range((testKey), 0, -1)).thenReturn(List.of("a", "b"));

        List<String> list = redisAdapter.getAllStringList(testKey);

        assertEquals(2, list.size());
    }

    @Test
    void getLastLongValue() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.rightPop(testKey)).thenReturn(123.0);

        Long result = redisAdapter.getLastLongValue(testKey);

        assertEquals(testLong, result);
    }

    @Test
    void delete() {
        when(redisTemplate.delete(testKey)).thenReturn(true);

        redisAdapter.delete(testKey);

        verify(redisTemplate).delete(testKey);
    }

    @Test
    void deleteListWithPrefix() {
        when(redisTemplate.keys(testKey + "*")).thenReturn(Set.of("testKey1", "testKey2", "testKey3"));
        when(redisTemplate.delete(anyCollection())).thenReturn(testLong);

        redisAdapter.deleteListWithPrefix(testKey);

        verify(redisTemplate).keys(testKey + "*");
        verify(redisTemplate).delete(anyCollection());
    }

    @Test
    void getBooleanFromHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get(testKey, testHashKey)).thenReturn(true);

        boolean result = redisAdapter.getBooleanFromHash(testKey, testHashKey);

        assertTrue(result);
    }

    @Test
    void getDoubleFromHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get(testKey, testHashKey)).thenReturn(123.0);

        Double result = redisAdapter.getDoubleFromHash(testKey, testHashKey);

        assertEquals(123d, result);
    }

    @Test
    void getStringFromHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get(testKey, testHashKey)).thenReturn(testValue);

        String result = redisAdapter.getStringFromHash(testKey, testHashKey);

        assertEquals(testValue, result);
    }

    @Test
    void setValueToHash() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        doNothing().when(hashOps).put(testKey, testHashKey, "true");

        redisAdapter.setValueToHash(testKey, testHashKey, true);

        verify(hashOps).put(testKey, testHashKey, "true");
    }

    @Test
    void setValueToHashTest() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        doNothing().when(hashOps).put(testKey, testHashKey, testValue);

        redisAdapter.setValueToHash(testKey, testHashKey, testValue);

        verify(hashOps).put(testKey, testHashKey, testValue);
    }

    @Test
    void getEntries() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.entries(testKey)).thenReturn(Map.of("test", "testVal"));

        Map<Object, Object> result = redisAdapter.getEntries(testKey);

        assertEquals("testVal", result.get("test"));
    }
}
