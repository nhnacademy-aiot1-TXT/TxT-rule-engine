package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.exception.NosuchRedisListException;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RedisAdapterTest {
    private SensorService sensorServiceMock;
    private RedisTemplate<String, Float> floatRedisTemplateMock;
    private RedisTemplate<String, String> stringRedisTemplateMock;
    private RedisTemplate<String, Double> doubleRedisTemplateMock;
    private RedisTemplate<String, Long> longRedisTemplateMock;
    private RedisAdapter redisAdapter;

    @BeforeEach
    public void setup() {
        sensorServiceMock = mock(SensorService.class);
        longRedisTemplateMock = mock(RedisTemplate.class);
        floatRedisTemplateMock = mock(RedisTemplate.class);
        stringRedisTemplateMock = mock(RedisTemplate.class);
        doubleRedisTemplateMock = mock(RedisTemplate.class);
        longRedisTemplateMock = mock(RedisTemplate.class);

        redisAdapter = new RedisAdapter(
                sensorServiceMock,
                longRedisTemplateMock,
                floatRedisTemplateMock,
                stringRedisTemplateMock,
                doubleRedisTemplateMock);
    }

    @Test
    public void testHasTimer() {
        String key = "test";
        when(stringRedisTemplateMock.hasKey(key)).thenReturn(true);

        boolean actualResult = redisAdapter.hasTimer(key);
        assertFalse(actualResult);
    }

    @Test
    public void testSetTimer() {
        String key = "test";
        Long time = 1000L;

        ValueOperations<String, Long> valueOperationsMock = mock(ValueOperations.class);
        when(longRedisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);

        redisAdapter.setTimer(key, time);

        Mockito.verify(valueOperationsMock).set(key + Constants.TIMER, time);
    }

    @Test
    public void testGetAllDoubleList() {
        String key = "test";

        ListOperations<String, Double> listOperationsMock = mock(ListOperations.class);

        when(doubleRedisTemplateMock.opsForList()).thenReturn(listOperationsMock);
        when(listOperationsMock.range(key, 0, -1)).thenReturn(null);

        Assertions.assertThrows(NosuchRedisListException.class, () -> {
            redisAdapter.getAllDoubleList(key);
        });
    }

    @Test
    public void testSaveFloatToList() {
        String key = "test";
        String value = "1.0f";

        ListOperations<String, Float> listOperationsMock = mock(ListOperations.class);

        when(floatRedisTemplateMock.opsForList()).thenReturn(listOperationsMock);
        when(sensorServiceMock.parseToFloatValue(value)).thenReturn(1.0f);

        redisAdapter.saveFloatToList(key, value);

        Mockito.verify(listOperationsMock).rightPush(key, 1.0f);
    }

    @Test
    public void testDelete() {
        String key = "test";

        redisAdapter.delete(key);

        Mockito.verify(stringRedisTemplateMock).delete(key);
    }

    @Test
    public void testSaveStringToList() {
        String key = "test";
        String value = "value";

        ListOperations<String, String> listOperationsMock = mock(ListOperations.class);
        when(stringRedisTemplateMock.opsForList()).thenReturn(listOperationsMock);

        redisAdapter.saveStringToList(key, value);

        Mockito.verify(listOperationsMock).rightPush(key, value);
    }

    @Test
    public void testGetLastLong() {
        String key = "test";
        Long value = 100L;

        ListOperations<String, Long> listOperationsMock = mock(ListOperations.class);
        when(longRedisTemplateMock.opsForList()).thenReturn(listOperationsMock);
        when(listOperationsMock.rightPop(key)).thenReturn(value);

        Long result = redisAdapter.getLastLong(key);

        Mockito.verify(listOperationsMock).rightPop(key);
        assertEquals(value, result);
    }

    @Test
    public void testDeleteListWithPrefix() {
        String prefix = "test*";

        Set<String> keys = new HashSet<>();
        keys.add("testKey1");
        keys.add("testKey2");

        when(stringRedisTemplateMock.keys(prefix + "*")).thenReturn(keys);

        redisAdapter.deleteListWithPrefix(prefix);

        Mockito.verify(stringRedisTemplateMock).delete(keys);
    }

    @Test
    public void testIsDevicePowered() {
        String deviceName = "device";
        String powerStatus = "true";

        HashOperations opsForHashMock = mock(HashOperations.class);
        when(stringRedisTemplateMock.opsForHash()).thenReturn(opsForHashMock);
        when(opsForHashMock.get("device_power_status", deviceName)).thenReturn(powerStatus);

        boolean isPowered = redisAdapter.isDevicePowered(deviceName);

        assertTrue(isPowered);
    }

    @Test
    public void testSetDevicePower() {
        String deviceName = "device";
        boolean power = true;

        HashOperations opsForHashMock = mock(HashOperations.class);
        when(stringRedisTemplateMock.opsForHash()).thenReturn(opsForHashMock);

        redisAdapter.setDevicePower(deviceName, power);


        Mockito.verify(opsForHashMock).put("device_power_status", deviceName, String.valueOf(power));
    }

    @Test
    public void testIsDeviceAutoMode() {
        String deviceName = "device";
        ValueOperations stringOpsMock = mock(ValueOperations.class);

        when(stringRedisTemplateMock.opsForValue()).thenReturn(stringOpsMock);
        when(stringOpsMock.get("auto_mode:" + deviceName)).thenReturn("true");

        boolean isAutoMode = redisAdapter.isDeviceAutoMode(deviceName);

        assertTrue(isAutoMode);
        verify(stringRedisTemplateMock.opsForValue(), times(1)).get("auto_mode:" + deviceName);
    }

    @Test
    public void testSaveHashes() {
        String key = "key";
        String hashKey = "hashKey";
        String value = "value";

        HashOperations opsForHashMock = mock(HashOperations.class);
        when(floatRedisTemplateMock.opsForHash()).thenReturn(opsForHashMock);

        redisAdapter.saveHashes(key, hashKey, value);

        verify(floatRedisTemplateMock.opsForHash(), times(1)).put(key, hashKey, value);
    }

    @Test
    public void testGetDoubleHashes() {
        String key = "key";
        String hashKey = "hashKey";
        Double expectedValue = 123.45;

        HashOperations opsForHashMock = mock(HashOperations.class);
        when(floatRedisTemplateMock.opsForHash()).thenReturn(opsForHashMock);
        when(floatRedisTemplateMock.opsForHash().get(key, hashKey)).thenReturn(expectedValue);

        Double actualValue = redisAdapter.getDoubleHashes(key, hashKey);

        assertEquals(expectedValue, actualValue);
        verify(floatRedisTemplateMock.opsForHash(), times(1)).get(key, hashKey);
    }
}