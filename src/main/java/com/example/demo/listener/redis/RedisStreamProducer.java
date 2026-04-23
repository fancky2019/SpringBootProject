package com.example.demo.listener.redis;

import com.example.demo.model.entity.demo.MqMessage;

public interface RedisStreamProducer {
    void sendMessage(String streamKey, MqMessage message);
}
