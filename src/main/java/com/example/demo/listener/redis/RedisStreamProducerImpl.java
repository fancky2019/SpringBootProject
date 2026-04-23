package com.example.demo.listener.redis;

import com.example.demo.listener.RouterMessageListener;
import com.example.demo.model.entity.demo.MqMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class RedisStreamProducerImpl implements RedisStreamProducer {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisStreamConfig redisStreamConfig;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private  RouterMessageListener routerMessageListener;



//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public class ApiMessage {
//        private String apiName;      // 路由标识：user-api, order-api, payment-api
//        private String requestId;    // 请求ID
//        private String method;       // GET, POST, PUT, DELETE
//        private String path;         // /api/user/info
//        private Map<String, Object> params;  // 请求参数
//        private Map<String, Object> body;    // 请求体
//        private Long timestamp;      // 时间戳
//    }






    /**
     * 发送 API 消息
     * @param streamKey Stream 的 Key（消息队列的名称）
     * @param message 请求数据
     */
    @Override
    public void sendMessage(String streamKey, MqMessage message) {
        try {
            ObjectRecord<String, Object> record = StreamRecords.newRecord()
                    .in(streamKey)
                    .ofObject(message);



//            1776750568095-0
            RecordId recordId = stringRedisTemplate.opsForStream().add(record);












//            // 将消息对象转换为 JSON 字符串
//            String jsonMessage = objectMapper.writeValueAsString(message);
//
//            // 构建消息体（Map<String, String>）
//            Map<String, String> body = new HashMap<>();
//            body.put("payload", jsonMessage);
//            body.put("timestamp", String.valueOf(System.currentTimeMillis()));
//            body.put("msgId", message.getMsgId());
//
//            // 创建 StringRecord
//            StringRecord record = StreamRecords.string(body)
//                    .withStreamKey(streamKey);
//
//            // 发送消息
//            RecordId recordId = redisTemplate.opsForStream().add(record);
//





//            // 1️⃣ 转 JSON
//            String json = objectMapper.writeValueAsString(message);
//
//            // 2️⃣ 构建 Map（Redis Stream 本质是 KV）
//            Map<String, String> body = new HashMap<>();
//            body.put("payload", json);
//            body.put("msgId", message.getMsgId());
//            body.put("ts", String.valueOf(System.currentTimeMillis()));
//
//            // 3️⃣ 创建 StringRecord
//            StringRecord record = StreamRecords.string(body)
//                    .withStreamKey(streamKey);
//
//            // 4️⃣ 正确的 MAXLEN 写法
//            RecordId recordId = stringRedisTemplate.opsForStream().add(
//                    record
//
//            );


//            RecordId recordId = stringRedisTemplate.opsForStream().add(
//                    record,
//                    org.springframework.data.redis.connection.stream.XAddOptions
//                            .maxlen(10000)
//                            .approximateTrimming(true)
//            );








            if (recordId != null) {
                log.info("消息发送成功: streamKey={}, msgId={}, recordId={}",
                        streamKey, message.getMsgId(), recordId);
            } else {
                log.error("消息发送失败: streamKey={}, msgId={}, 返回recordId为null",
                        streamKey, message.getMsgId());
                throw new RuntimeException("消息发送失败");
            }
        } catch (Exception e) {
            log.error("消息发送异常: streamKey={}, msgId={}", streamKey, message.getMsgId(), e);
            throw new RuntimeException("消息发送异常", e);
        }
    }
}
