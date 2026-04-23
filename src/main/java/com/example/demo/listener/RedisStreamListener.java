package com.example.demo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class RedisStreamListener  implements StreamListener<String, ObjectRecord<String, Object>> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(ObjectRecord<String, Object> message) {
        try {
            // 1. 业务逻辑处理...
            System.out.println("处理消息: " + message.getValue());

            // 2. 处理成功后，手动确认消息
            Long ackCount = redisTemplate.opsForStream().acknowledge("my-group", message);
            System.out.println("消息已确认，ACK结果: " + ackCount);
        } catch (Exception e) {
            // 处理失败，不ACK，消息将留在PEL中等待重试
            System.err.println("消息处理失败，将重试: " + e.getMessage());
        }
    }
}