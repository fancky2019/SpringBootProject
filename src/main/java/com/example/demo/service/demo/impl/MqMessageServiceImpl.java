package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.dao.demo.MqMessageMapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.MqMessageRequest;
import com.example.demo.model.response.MqMessageResponse;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.service.demo.IMqMessageService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.service.demo.IProductTestService;
import com.example.demo.utility.MqSendUtil;
import com.example.demo.utility.RedisKeyConfigConst;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
@Slf4j
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

    @Autowired
    private MqSendUtil mqSendUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private Executor executor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MqMessage mqMessage) {
        this.save(mqMessage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MqMessage mqMessage) throws Exception {
//        this.updateById(mqMessage);
//        this.update(mqMessage, new LambdaUpdateWrapper<MqMessage>().eq(MqMessage::getId, mqMessage.getId()));

        Integer oldVersion = mqMessage.getVersion();
        mqMessage.setVersion(mqMessage.getVersion() + 1);
        mqMessage.setModifyTime(LocalDateTime.now());
        LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<MqMessage>();
        updateWrapper.eq(MqMessage::getVersion, oldVersion);
        updateWrapper.eq(MqMessage::getId, mqMessage.getId());
        boolean re = this.update(mqMessage, updateWrapper);
        if (!re) {
            String message = MessageFormat.format("MqMessage update fail :id - {0} ,version - {1}", mqMessage.getId(), oldVersion);
            throw new Exception(message);
        }
    }

    @Override
    public PageData<MqMessageResponse> list(MqMessageRequest mqMessage) throws JsonProcessingException {
        MqMessage message = this.getById(7);
        String json = objectMapper.writeValueAsString(message);

        // 设置时区为 GMT+8   UTC
        ZonedDateTime zonedDateTime = ZonedDateTime.now();

        //ZonedDateTime序列化  ZonedDateTime jackson
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(zoneId);
        //要带Z 否则jackson序列化异常
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //2025-03-12T07:09:12.445Z
        String utcDateTimeStr = utcDateTime.format(formatter);
        String jsonZone = objectMapper.writeValueAsString(zonedDateTime);
        ZonedDateTime tt = objectMapper.readValue(jsonZone, ZonedDateTime.class);


        LambdaQueryWrapper<MqMessage> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(MqMessage::getStatus, 2);
        //排序
//        queryWrapper.orderByDesc(User::getAge)
//                .orderByAsc(User::getName);
        queryWrapper.orderByDesc(MqMessage::getId);
        List<MqMessage> mqMessageList = this.list(queryWrapper);
        List<MqMessageResponse> mqMessageResponseList = mqMessageList.stream().map(p -> {
            MqMessageResponse response = new MqMessageResponse();
            BeanUtils.copyProperties(p, response);
            return response;
        }).collect(Collectors.toList());
        PageData<MqMessageResponse> pageData = new PageData<>();
        pageData.setData(mqMessageResponseList);
        return pageData;
    }

    @Override
    public void page(MqMessageRequest mqMessage) {

    }

    @Override
    public void count(MqMessageRequest mqMessage) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MqMessage mqMessage) {
        this.remove(new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId, mqMessage.getId()));
    }

//    @Override
//    public MqMessage getById(Long id) {
//        return this.getOne(new LambdaQueryWrapper<MqMessage>().eq(MqMessage::getId, id));
//    }
//
//    @Override
//    public List<MqMessage> list() {
//
//    }

    /**
     * 生产失败的重新发布到mq，消费失败的
     */
    @Override
    public void mqOperation() {
//        LambdaQueryWrapper<MqMessage> queryWrapper = new LambdaQueryWrapper<>();
//        //没有消费确认=发布失败+消费失败
//        queryWrapper.eq(MqMessage::getStatus, 2);
//        List<MqMessage> mqMessageList = this.list(queryWrapper);
//        List<MqMessage> unPushList = mqMessageList.stream().filter(p -> p.getStatus().equals(0)).collect(Collectors.toList());
//        //可设计单独的job 处理消费失败
//        List<MqMessage> consumerFailList = mqMessageList.stream().filter(p ->  p.getStatus().equals(1) &&  p.getStatus().equals(2)).collect(Collectors.toList());
//        rePublish(unPushList);
//        reConsume(consumerFailList);


        log.info("start executing mqOperation");

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String operationLockKey = RedisKeyConfigConst.MQ_FAIL_HANDLER;
        //并发访问，加锁控制
        RLock lock = redissonClient.getLock(operationLockKey);

        try {
            long waitTime = 10;
            long leaseTime = 30;
            boolean lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (lockSuccessfully) {
                //无论成功失败都会更新一次数据库，使得UpdateTime 变更保持索引的数据少
                //联合索引（UpdateTime，Status）
                LocalDateTime startQueryTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timeStr = formatter.format(LocalDateTime.now());

                //将更新时间写入redis
                String latestExecutingTimeRedis = valueOperations.get(RedisKeyConfigConst.MQ_FAIL_HANDLER_TIME);
                //redis key 不存在
                if (StringUtils.isEmpty(latestExecutingTimeRedis)) {
                    startQueryTime = null;
                } else {
                    startQueryTime = LocalDateTime.parse(latestExecutingTimeRedis, formatter);
                }
                valueOperations.set(RedisKeyConfigConst.MQ_FAIL_HANDLER_TIME, timeStr);

                LambdaQueryWrapper<MqMessage> queryWrapper = new LambdaQueryWrapper<>();
                //mybatis-plus and or
//                //没有消费确认  (AandB)or(C)
//                queryWrapper.and(p->p.ne(MqMessage::getStatus, 2).eq(MqMessage::getId, 83));
////                queryWrapper.or(p->p.ne(MqMessage::getStatus, 2).eq(MqMessage::getId, 83));
//                queryWrapper.or(p->p.eq(MqMessage::getStatus,null));
//                mysql null 不运算 <>
                //<, <=, >, >=, <>  lt()，le()，gt()，ge()，ne()
                if (startQueryTime != null) {
                    queryWrapper.gt(MqMessage::getModifyTime, startQueryTime);
                }
//                queryWrapper.ne(MqMessage::getStatus, 2);
                queryWrapper.and(p -> p.isNull(MqMessage::getStatus).or(m -> m.ne(MqMessage::getStatus, 2)));
                List<MqMessage> mqMessageList = this.list(queryWrapper);
                if (CollectionUtils.isEmpty(mqMessageList)) {
                    return;
                }
                if (true) {
                    return;
                }
                //未推送消息(未推送，推送失败
                List<MqMessage> unPushList = mqMessageList.stream().filter(p -> p.getStatus() == null || p.getStatus().equals(0)).collect(Collectors.toList());
                //可设计单独的job 处理消费失败
                List<MqMessage> consumerFailList = mqMessageList.stream().filter(p -> p.getStatus() != null && p.getStatus().equals(1)).collect(Collectors.toList());
                rePublish(unPushList);

                Object proxyObj = AopContext.currentProxy();
                IMqMessageService mqMessageService = null;
                if (proxyObj instanceof IMqMessageService) {
                    mqMessageService = (IMqMessageService) proxyObj;
                }
                mqMessageService.reConsume(consumerFailList);


            } else {
                //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                //超过waitTime ，扔未获得锁
                log.info("mqFailHandler:获取锁失败");
            }
        } catch (Exception e) {
            // throw  e;
            log.error("", e);
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            //unlock 删除key
            lock.unlock();
        }


    }

    @Override
    public void rePublish(List<MqMessage> mqMessageList) {
//        CompletableFuture.runAsync(() ->
//        {
//            publish(mqMessageList);
//        });

        executor.execute(() -> {
            publish(mqMessageList);
//            // 模拟耗时操作
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Task completed.");
        });
    }

    private synchronized void publish(List<MqMessage> mqMessageList) {

        log.info("start executing publish");
        try {
            for (MqMessage message : mqMessageList) {
                mqSendUtil.send(message);
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    /**
     * 加了 @Async  和xxl-hob 的线程池 不在同一个线程，使用mqFailHandlerExecutor 线程池
     * @param mqMessageList
     * @throws Exception
     */
    @Async("mqFailHandlerExecutor")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reConsume(List<MqMessage> mqMessageList) throws Exception {
//        CompletableFuture.runAsync(() ->
//        {
//            consume(mqMessageList);
//        });

        log.info("start executing reConsume");
        consume(mqMessageList);
    }


    private synchronized void consume(List<MqMessage> mqMessageList) throws Exception {

        for (MqMessage message : mqMessageList) {
            switch (message.getQueue()) {
                case RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME:
                    //执行对应的service 消费代码

                    boolean consumeSuccess = true;
                    if (consumeSuccess) {
                        //update message status
                        message.setStatus(2);
                        this.update(message);
                    } else {
                        //失败了就更新一下版本号和更新时间，根据更新时间的 索引 提高查询速度
                        this.update(message);
                    }

//                    if (message.getId() % 2 != 0) {
//                        throw new Exception("test");
//                    }
                    break;
                default:
                    break;
            }
        }

    }
}
