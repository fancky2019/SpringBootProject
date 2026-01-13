package com.example.demo.rabbitMQ;

import com.example.demo.model.entity.demo.MqFailLog;
import com.example.demo.model.enums.MqMessageStatus;
import com.example.demo.service.demo.IMqFailLogService;
import com.example.demo.service.demo.IMqMessageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * 死信队列（DLQ）
 * 被消费者拒绝（basic.reject/basic.nack）且 requeue=false
 * 消息过期（TTL到期）
 * 队列达到最大长度限制
 *
 * @author ruili
 */
public class BaseRabbitMqHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseRabbitMqHandler.class);

    private static final String RABBIT_MQ_MESSAGE_ID_PREFIX = "rabbitMQ:messageId:";
    //
    private static final int TOTAL_RETRY_COUNT = 1;
    private static final int EXPIRE_TIME = 24 * 60 * 60;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    IMqFailLogService mqFailLogService;

    @Autowired
    IMqMessageService mqMessageService;

    //    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ObjectMapper objectMapper;


    public <T> void onMessage(Class<T> tClass, Message message, Channel channel, Consumer<T> consumer) {
        MessageProperties messageProperties = message.getMessageProperties();
        String businessKey = messageProperties.getHeader("businessKey");
        String businessId = messageProperties.getHeader("businessId");
        String msgId = messageProperties.getMessageId();
        String traceId = messageProperties.getHeader("traceId");
        Boolean retry = messageProperties.getHeader("retry");

        String messageId = message.getMessageProperties().getMessageId();
        String msgContent = new String(message.getBody());
        String mqMsgIdKey = RABBIT_MQ_MESSAGE_ID_PREFIX + messageId;

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        //添加重复消费redis 校验，不会存在并发同一个message
        Object retryCountObj = valueOperations.get(mqMsgIdKey);
//        String time1 = LocalDateTimeUtil.formatNormal(t.getMessageTime());
//        String time2 = LocalDateTimeUtil.formatNormal(LocalDateTime.now());
//        logger.info("time1 - {} time2 - {}", time1, time2);
        logger.info("开始消费msg - {}", messageId);
        int retryCount = 0;
        try {

            if (retryCountObj == null) {
                //value 重试次数
                valueOperations.set(mqMsgIdKey, 0);
            } else {
                //没有过期时间,说明没有消费成功
                if (redisTemplate.getExpire(mqMsgIdKey) == -1) {
                    retryCount = (int) retryCountObj;
                    //没有重试
                    if (retryCount == 0) {
                        long deliveryTag = message.getMessageProperties().getDeliveryTag();
                        //补偿 ack--消费了却没有ack 成功。
                        channel.basicAck(deliveryTag, false);
                        logger.info("msgId - {} 已经被消费,msg - {}", messageId, msgContent);
                        return;
                    }
                } else {
                    logger.info("msgId - {} 已经被消费,msg - {}", messageId, msgContent);
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    return;
                }


            }
            T t = objectMapper.readValue(msgContent, tClass);
            consumer.accept(t);
//             int i = Integer.parseInt("m");

//            //失败入队测试
//            long deliveryTag = message.getMessageProperties().getDeliveryTag();
////            void basicReject(long deliveryTag, boolean requeue) throws IOException;
////            channel.basicReject(deliveryTag, true);
//            if (channel != null && channel.isOpen()) {
//                channel.basicReject(deliveryTag, false);
//            }
            //消费成功设置过期时间删除key.
            if (redisTemplate.expire(mqMsgIdKey, EXPIRE_TIME, TimeUnit.SECONDS)) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                logger.info("消费成功：{}", messageId);
            }


        } catch (Exception e) {
            logger.info("消息消费失败：", e);
            logger.info("消息消费失败 - {}", msgContent);
            try {
                /**
                 * deliveryTag:该消息的index
                 * multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
                 * requeue：被拒绝的是否重新入队列
                 */
                //channel.basicNack(deliveryTag, false, true);

//                //失败入队测试
//                long deliveryTag = message.getMessageProperties().getDeliveryTag();
//                //void basicReject(long deliveryTag, boolean requeue) throws IOException;
//                channel.basicReject(deliveryTag, true);

                //延迟重试处理
                this.retry(channel, message, retryCount, e.getMessage());

                //捕获多个类型的异常
//            } catch (IOException | InterruptedException ex) {
            } catch (Exception ex) {
                logger.info("被拒绝的消息重新入队列出错", ex);
            }
        }
    }

    private void retry(Channel channel, Message message, int retryCount, String exceptionMsg) throws Exception {
        //   String redisCountKey = "retry:" + RabbitMqConstants.TB_CUST_LIST_ERROR_QUEUE + t.getMessageId();

        String messageId = message.getMessageProperties().getMessageId();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String mqMsgIdKey = RABBIT_MQ_MESSAGE_ID_PREFIX + messageId;
        boolean requeue = ++retryCount <= TOTAL_RETRY_COUNT;
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if (requeue) {

            //region 重新入队建议：使用 死信队列（DLX） 记录失败消息结合 重试次数计数器 避免无限循环
            //void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException;
           /*
           默认情况下会插入到队列的尾部（即队尾），而不是队首。
           公平性：避免同一消息被反复优先处理，导致其他消息饥饿（Starvation）。
           顺序性：保持队列的 FIFO（先进先出）特性，避免消息乱序。
            1. 未确认消息何时重新入队？
            消费者断开连接（崩溃、网络故障等）时，所有未 ack 的消息会被重新入队。
            显式调用 basicNack 或 basicReject 并设置 requeue=true 时，消息会被重新入队。

        2. 重新入队的位置
        默认行为：重新入队的消息会被放到原队列的尾部（队尾），等待被其他消费者按顺序消费。
        不会插入队首，除非使用某些特定插件或策略（如优先级队列）。


            deliveryTag (long 类型)
            消息的唯一标识符（投递标签）
            由 RabbitMQ 在消息投递时分配
                    用于指定要拒绝的特定消息
            multiple (boolean 类型)
                true：拒绝所有比当前 deliveryTag 小的未确认消息（批量拒绝）
                false：仅拒绝当前指定的 deliveryTag 消息
            requeue (boolean 类型)
                true：被拒绝的消息会重新放回队列，可以被其他消费者（或同一消费者）再次消费
                false：消息会被直接丢弃（如果配置了死信队列，则进入死信队列）

             basicReject 是 basicNack 的单条消息版本：
            // 这两行代码效果相同
            channel.basicReject(deliveryTag, true);
            channel.basicNack(deliveryTag, false, true);

            使用 Nack 后，消息会从 Ready 状态变为 Unacked 状态
            如果 requeue=true，消息会重新变为 Ready 状态
            如果消费者在处理消息时崩溃，所有 Unacked 消息会自动重新入队
            批量拒绝时，deliveryTag 应该是当前最大的那个标签
                */
            //endregion

            //void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException;
            channel.basicNack(deliveryTag, false, false);
            valueOperations.set(mqMsgIdKey, retryCount);
            logger.info(" {} 开始第{}次回归到队列：", deliveryTag, retryCount);
        } else {
            //ack 掉消息，把该消息插入数据库，批处理
            if (redisTemplate.expire(mqMsgIdKey, EXPIRE_TIME, TimeUnit.SECONDS)) {
                channel.basicAck(deliveryTag, false);
            }
            String msgContent = new String(message.getBody());
            HashMap<String, String> map = objectMapper.readValue(msgContent, new TypeReference<HashMap<String, String>>() {
            });
            String id = "";
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("id")) {
                    id = entry.getValue();
                    break;
                }
            }

            //region MqFailLog
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            String exchange = message.getMessageProperties().getReceivedExchange();
            String queueName = message.getMessageProperties().getConsumerQueue();
            //错误日志入库。插入消息表，不在单独设计MqFailLog
            MqFailLog mqFailLog = new MqFailLog();
            mqFailLog.setMsgContentId(id);
            mqFailLog.setExchange(exchange);
            mqFailLog.setQueueName(queueName);
            mqFailLog.setRoutingKey(routingKey);
            mqFailLog.setMsgId(id);
            mqFailLog.setMessage(msgContent);
            mqFailLog.setCause(exceptionMsg);
            mqFailLogService.save(mqFailLog);
            //endregion

            //设置过期删除key
            redisTemplate.expire(mqMsgIdKey, EXPIRE_TIME, TimeUnit.SECONDS);

            //region update mqMessage
            //重试仍然没有成功，标记为消费失败。走定时任务补偿
//            mqMessageService.updateByMsgId(messageId, MqMessageStatus.CONSUME_FAIL.getValue());
            mqMessageService.updateByMsgIdAsync(messageId, MqMessageStatus.CONSUME_FAIL.getValue());
            //endregion

//            ThreadLocal<String> threadLocal = new ThreadLocal<>();
//            expungeStale
        }

    }
}
