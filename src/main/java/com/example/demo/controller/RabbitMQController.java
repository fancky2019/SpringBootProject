package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.rabbitMQ.RabbitMqManager;
import com.example.demo.utility.MqSendUtil;
import io.netty.util.concurrent.CompleteFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/rabbitMQ")
public class RabbitMQController {

    @Autowired
    private RabbitMQTest rabbitMQTest;

    @Autowired
    private RabbitMqManager rabbitMqManager;

    @Autowired
    private MqSendUtil mqSendUtil;

    @GetMapping("")
    public MessageResult<Void> rabbitMQTest() {

        /*
          1、将消息和数据库业务对象一起提交
          2、数据库事务完成调用事务后置增强回调，发送rabbitMQ消息
          3、rabbitMQ 生产成功，回调将消息状态设置成已生产。
          4、rabbit 生产失败
        */
       //rabbitMQ事务回调发送封装在  MqSendUtil

        rabbitMQTest.test();


        return null;
    }

    /*
    Retryable  切面 动态代理，通过代理对象访问
    @Retryable不能在本类使用，不然不会生效。如果直接调用execute重试机制将不会生效，调用devide则重试生效。
    @Retryable注解:
    value: 抛出指定异常才会重试
    include：和value一样，默认为空，当exclude也为空时，默认所以异常
    exclude：指定不处理的异常
    maxAttempts：最大重试次数，默认3次
    backoff：重试等待策略，默认使用@Backoff，@Backoff的value默认为1000L；multiplier（指定延迟倍数）

    @Recover注解：
    当重试达到指定次数时候该注解的方法将被回调
    发生的异常类型需要和@Recover注解的参数一致
    @Retryable注解的方法不能有返回值，不然@Recover注解的方法无效

     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public void send() {
        CompletableFuture.runAsync(() -> {
            //内部异常是否能抛出到外部线程
            // 事务提交之后，则执行我们的目标方法
//                try {
//                    rabbitMQTest.test();
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }

            rabbitMQTest.test();
        });
    }

    @GetMapping("/getMessageCount")
    public MessageResult<Void> getMessageCount(String queueName) {
        try {
            rabbitMQTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}
