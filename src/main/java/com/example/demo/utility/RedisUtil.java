package com.example.demo.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * @author lirui
 */
@Component
public class RedisUtil<K, V> {
    @Autowired
    private RedisTemplate redisTemplate;

    public V get(K key) {
        ValueOperations<K, V> valueOperations = redisTemplate.opsForValue();

        V val = valueOperations.get(key);

        return val;
    }


}
