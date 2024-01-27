package com.example.demo.service;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.utility.RedisKeyConfigConst;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;

    public MessageResult<String> getRepeatToken(String apiName) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        BigInteger userId = new BigInteger("1");

        String key = "repeat:" + userId + ":" + apiName;
        // Object val = valueOperations.get(key);//null
        /*
           redis  ttl
       如果key不存在或者已过期，返回 -2
       如果key存在并且没有设置过期时间（永久有效），返回 -1 。

         */

        String operationLockKey = key + RedisKeyConfigConst.KEY_LOCK_SUFFIX;


        String repeatToken = "";
        //设置要加锁 防止一个用户多点登录并发操作
        RLock lock = redissonClient.getLock(operationLockKey);
        try {


            //tryLock(long waitTime, long leaseTime, TimeUnit unit)
            //获取锁等待时间
            long waitTime = 1;
            //持有所超时释放锁时间  24 * 60 * 60;
            long leaseTime = 30;
            boolean lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (lockSuccessfully) {

            }


            Long expireTime = redisTemplate.getExpire(key, TimeUnit.DAYS);//-2

//        if (expireTime.==-1))
            if (expireTime.equals(-1L)) {
                /*直接返回,此处没有设置过期时间key 会一直存在。造成空间浪费
                此处设置key完成过期时间和任务完成的过期时间会有冲突，可以设计key 的 value 数据结构
                {token,createTime,status} ,启动定时任务 获取key ,过来没有过期时间的，判断createTime
                是否大于阈值，大于就删除
                */
                repeatToken = valueOperations.get(key).toString();//null
            } else {
                //插入
                repeatToken = UUID.randomUUID().toString().replace("-", "");
                valueOperations.set(key, repeatToken);
            }
        } catch (Exception ex) {
            log.error("", ex);
            MessageResult.faile("获取token失败");
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            //unlock 删除key
            lock.unlock();
        }
        return MessageResult.success(repeatToken);
    }
}
