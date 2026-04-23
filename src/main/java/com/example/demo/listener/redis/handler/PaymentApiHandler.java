package com.example.demo.listener.redis.handler;

import com.example.demo.listener.redis.RedisStreamHandlerBeanName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
//默认beanName 是类名首字母小写
//@Service
@Service(RedisStreamHandlerBeanName.PAYMENT_SERVICE)
public class PaymentApiHandler implements RedisStreamHandler {
    @Override
    public void handle(Object message) {
        log.info("处理用户 API 消息: {}", message);
        // 具体业务逻辑
    }
}
