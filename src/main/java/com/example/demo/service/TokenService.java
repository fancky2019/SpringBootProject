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

    /**
     * 设计思路
     * 防止重复提交的核心是使用Token机制，主要思路如下：
     *
     * 服务器生成唯一Token并存储在Session或缓存中
     *
     * Token随表单返回给客户端
     *
     * 客户端提交表单时携带Token
     *
     * 服务器验证Token有效性并删除使用过的Token
     *
     * 防止重复提交的核心是"一次有效"原则
     * @param apiName
     * @return
     */
    public MessageResult<String> getRepeatToken(String apiName) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        BigInteger userId = new BigInteger("1");
        ///sbp/demo/demoProductTest
//        String uri = httpServletRequest.getRequestURI();
        //repeat:1:/api/applyReceiptOrderItem/createWorkingDirectory
        String keyWithOutToken = "repeat:" + userId + ":" + apiName;
        // Object val = valueOperations.get(key);//null
        /*
           redis  ttl
       如果key不存在或者已过期，返回 -2
       如果key存在并且没有设置过期时间（永久有效），返回 -1 。

         */
        String operationLockKey = keyWithOutToken + ":" + RedisKeyConfigConst.KEY_LOCK_SUFFIX;
//        String operationLockKey = tokenKey + ":"+ RedisKeyConfigConst.KEY_LOCK_SUFFIX;

//        String operationLockKey = key + RedisKeyConfigConst.KEY_LOCK_SUFFIX;

//基于token 的方式：logAspect 切面完成时候加redisson 锁tokenKey 的operationLockKey。在锁内设置过期时间，
//        如果并发获取了正在执行的token,redisson获取锁时候回判断过期时间
        //key 没有设置过期时间完成，就继续访问接口。但是logAspect有过期时间判断，在极端情况下如果过期时间没有设置成功，还有并发

        String repeatToken = "";
        //设置要加锁 防止一个用户多点登录并发操作


//            Long expireTime = redisTemplate.getExpire(key);
//            //有过期时间
//            if (expireTime != null && !expireTime.equals(-1L)) {
//                return MessageResult.faile("DuplicateSubmission!");
//            }
            /*
             * getExpire 返回值含义
             * null → Redis 里根本没有这个 key。
             * -1 → key 存在，但没有设置过期时间。
             * -2 → key 已经不存在（过期或被删）。
             * >=0 → 剩余的秒数。
             */

            Long expireTime = redisTemplate.getExpire(keyWithOutToken, TimeUnit.DAYS);//-2

//        if (expireTime.==-1))
            if (Long.valueOf(-1L).equals(expireTime)) {
                //存在
                /*直接返回,此处没有设置过期时间key 会一直存在。造成空间浪费
                此处设置key完成过期时间和任务完成的过期时间会有冲突，可以设计key 的 value 数据结构
                {token,createTime,status} ,启动定时任务 获取key ,过来没有过期时间的，判断createTime
                是否大于阈值，大于就删除
                */
                repeatToken = valueOperations.get(keyWithOutToken).toString();//null
            } else {
                //插入
                repeatToken = UUID.randomUUID().toString().replace("-", "");
                valueOperations.set(keyWithOutToken, repeatToken);
//                String keyWithToken=keyWithOutToken+":"+repeatToken;
//                valueOperations.set(keyWithOutToken, keyWithToken);

            }

//           if (expireTime != null && !expireTime.equals(-1L)) {
////                return MessageResult.faile("DuplicateSubmission!");
//               repeatToken = valueOperations.get(keyWithOutToken).toString();
//            }
//           else {
//               //插入
//               repeatToken = UUID.randomUUID().toString().replace("-", "");
//               valueOperations.set(keyWithOutToken, repeatToken);
//           }



        return MessageResult.success(repeatToken);
    }
}
