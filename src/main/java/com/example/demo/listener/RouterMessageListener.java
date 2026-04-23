package com.example.demo.listener;

import com.example.demo.listener.redis.*;
import com.example.demo.listener.redis.handler.RedisStreamHandler;
import com.example.demo.model.entity.demo.MqMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.AutoClaimResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Range;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RouterMessageListener implements StreamListener<String, ObjectRecord<String, MqMessage>>, ApplicationContextAware {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisStreamConfig redisStreamConfig;
    // Spring 会自动将所有的 RedisStreamHandler 注入到这个 Map 中
    // key 就是 @Service 指定的 Bean 名称（如 "user-api"）,默认beanName 是类名首字母小写
    // value 是对应的 Handler 实例
    @Getter
    private final Map<String, RedisStreamHandler> handlerMap;

    public RouterMessageListener(Map<String, RedisStreamHandler> handlerMap) {
        this.handlerMap = handlerMap;
        log.info("RedisStreamRouter 初始化完成，共注册 {} 个处理器: {}", handlerMap.size(), handlerMap.keySet());
    }

    //        private final Map<String, RedisStreamHandler> handlerMap = new HashMap<>();
//    @Autowired
//    private UserApiHandler userHandler;
//
//    @Autowired
//    private OrderApiHandler orderHandler;
//
//    @Autowired
//    private PaymentApiHandler paymentHandler;
//
//    @PostConstruct
//    public void init() {
//        handlerMap.put("user-api", userHandler);
//        handlerMap.put("order-api", orderHandler);
//        handlerMap.put("payment-api", paymentHandler);
//    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        // 获取所有实现了 RedisStreamHandler 接口的 Bean
        Map<String, RedisStreamHandler> handlers = context.getBeansOfType(RedisStreamHandler.class);

//        for (Object bean : beans.values()) {
//            if (bean instanceof RedisStreamHandler) {
//                RedisStreamHandler annotation = bean.getClass().getAnnotation(RedisStreamHandler.class);
//                String apiType = annotation.value();
//                handlerMap.put(apiType, (RedisStreamHandler) bean);
//                log.info("注册处理器: {} -> {}", apiType, bean.getClass().getSimpleName());
//            }
//        }


//        // 获取所有带有 @ApiHandlerType 注解的 Bean
//        Map<String, Object> beans = context.getBeansWithAnnotation(ApiHandlerType.class);
//
//        for (Object bean : beans.values()) {
//            if (bean instanceof ApiHandler) {
//                ApiHandlerType annotation = bean.getClass().getAnnotation(ApiHandlerType.class);
//                String apiType = annotation.value();
//                handlerMap.put(apiType, (ApiHandler) bean);
//                log.info("注册处理器: {} -> {}", apiType, bean.getClass().getSimpleName());
//            }
//        }
    }

    @Override
    public void onMessage(ObjectRecord<String, MqMessage> message) {
        String streamKey = message.getStream();
        MqMessage value = message.getValue();

        try {
            RedisStreamHandler handler = handlerMap.get(streamKey);
            if (handler != null) {
                handler.handle(message.getValue());
                // 手动 ACK
//                acknowledge(message);
            } else {
                log.warn("未找到对应的处理器: apiName={}", streamKey);
            }
        } catch (Exception e) {
            log.error("处理消息失败: stream={}, error={}", streamKey, e.getMessage(), e);
            // 不 ACK，消息会留在 PEL 中等待重试
        }
    }


    /**
     * 手动确认消息
     *
     *
     * 查看 未消费的消息数量
     * XPENDING your-stream-key your-group-name
     * XPENDING userApi processor-group-1
     *
     *ack之后不会删除，把这条消息从消费者组的 Pending Entries List（PEL）里移除
     *
     * Redis Stream 没有“只删除已 ACK 消息”的原生能力
     * Kafka 可以：按 offset 删除、按消费进度控制
     *
     *  Redis Stream 做不到：❌ 按 ACK 精确删除、❌ 自动 GC 已消费数据
     * @param message 消息对象
     */
    private void acknowledge(ObjectRecord<String, MqMessage> message) {
        String streamKey = message.getStream();
        RecordId recordId = message.getId();
        try {

            String groupName = redisStreamConfig.getGroupName();
            // 执行 ACK

            // acknowledge 方法返回 Long，表示实际确认的消息数量
            Long ackCount = stringRedisTemplate.opsForStream()
                    .acknowledge(streamKey, groupName, recordId);

            if (ackCount != null && ackCount > 0) {
                log.info("ACK成功: streamKey={}, recordId={}, 确认数量={}",
                        streamKey, recordId, ackCount);

                //不做多消费者组是可以的，不然其他消费者组还没消费就删除了
                // 2. 删除已 ACK 的消息（单消费者组场景安全）
                Long delCount = stringRedisTemplate.opsForStream()
                        .delete(streamKey, recordId);

                if (delCount != null && delCount > 0) {
                    log.info("删除成功: streamKey={}, recordId={}", streamKey, recordId);
                }

            } else {
                log.warn("ACK失败: streamKey={}, recordId={}, 确认数量=0",
                        streamKey, recordId);
                throw new RuntimeException("ACK失败，确认数量为0");
            }
        } catch (Exception e) {
            log.error("ACK异常: streamKey={}, recordId={}", streamKey, recordId, e);
            throw new RuntimeException("ACK异常", e);
        }
    }

    private static final int MAX_RETRY_COUNT = 3;
    private static final long CLAIM_TIMEOUT_MS = 60000; // 60秒超时


    // 每 10 秒扫描一次未ACK消息
    @Scheduled(fixedDelay = 10000)
    public void retryTimeoutMessages() {

        String streamKey = RedisStreamHandlerBeanName.USER_SERVICE;
        String groupName = redisStreamConfig.getGroupName();
        String consumerName = redisStreamConfig.getConsumerName();

        claimTimeoutMessages(streamKey, groupName, consumerName);
    }


    private void claimTimeoutMessages(String streamKey, String groupName, String consumerName) {

        try {
            // 1️⃣ 先获取超时的 pending 消息ID列表
            // 获取所有 Pending 消息（最多100条）
            long maxPendingCount = 100;
            PendingMessages pending = stringRedisTemplate.opsForStream()
                    .pending(streamKey, groupName, Range.unbounded(), maxPendingCount);

            if (pending == null || pending.isEmpty()) {
                return;
            }

            // 2️⃣ 收集需要认领的消息ID
            List<RecordId> messageIds = new ArrayList<>();
            for (PendingMessage message : pending) {
                messageIds.add(message.getId());
            }

            // 3️⃣ 批量认领:认领超时(CLAIM_TIMEOUT_MS)的消息
//            claim 执行后，消息的 idle_time（空闲时间）被重置为 0
            List<MapRecord<String, Object, Object>> records =
                    stringRedisTemplate.opsForStream()
                            .claim(
                                    streamKey,
                                    groupName,
                                    consumerName,
                                    Duration.ofMillis(CLAIM_TIMEOUT_MS),
                                    messageIds.toArray(new RecordId[0])
                            );

            if (records == null || records.isEmpty()) {
                return;
            }

            // 4️⃣ 处理认领的消息
            for (MapRecord<String, Object, Object> record : records) {
                try {

                   MqMessage mqMessage = new MqMessage();
                    Map mapVal = record.getValue();
                    mqMessage.toMessage(mapVal);

                    // 执行业务逻辑
//                    handle(mqMessage);

                    // ACK 确认
                    stringRedisTemplate.opsForStream().acknowledge(streamKey, groupName, record.getId());

                    log.info("重试成功: {}", record.getId());

                } catch (Exception e) {
                    log.error("重试失败: {}", record.getId(), e);
                    // 不 ACK，消息会重新进入 PEL，下次继续认领
                }
            }

        } catch (Exception e) {
            log.error("claimTimeoutMessages 执行异常: stream={}, group={}", streamKey, groupName, e);
        }
    }


    /**
     * 认领超时未 ACK 的消息（实现重试）
     */
    private void claimTimeoutMessages1(String streamKey, String groupName, String consumerName) {

        try {

            // ⚠️ 直接批量 claim（核心优化点）
            List<MapRecord<String, Object, Object>> records =
                    stringRedisTemplate.opsForStream()
                            .claim(
                                    streamKey,
                                    groupName,
                                    consumerName,
                                    Duration.ofMillis(CLAIM_TIMEOUT_MS), // idle threshold
                                    RecordId.of("0-0") // 起始点（批量模式）
                            );

            if (records == null || records.isEmpty()) {
                return;
            }

            for (MapRecord<String, Object, Object> record : records) {

                try {
                    // ✔ 重新处理
//                    handle(record);

                    // ✔ ACK
                    stringRedisTemplate.opsForStream()
                            .acknowledge(streamKey, groupName, record.getId());

                    log.info("重试成功: {}", record.getId());

                } catch (Exception e) {
                    log.error("重试失败: {}", record.getId(), e);
                    // ❗不ACK → 下轮继续 claim
                }
            }

        } catch (Exception e) {
            log.error("claim定时任务异常", e);
        }
    }


}

