package com.example.demo.utility;

import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.service.demo.DemoProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MqSendUtil {
    private static final Logger logger = LogManager.getLogger(DemoProductService.class);

    @Autowired
    private RabbitMQTest rabbitMQTest;

    /**
     * synchronized
     *
     * @param mqMessage
     */
    public void send(MqMessage mqMessage) {
        //处理事务回调发送信息到mq
        //boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        // 判断当前是否存在事务,如果没有开启事务是会报错的
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 无事务，
            sentAsync(mqMessage);
            return;
        }

        //事务回调：事务同步，此处待处理，
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//            @Override
//            public void afterCompletion(int status) {
//                //限制性事务afterCommit，然后执行afterCompletion
//                //afterCommit,afterCompletion
//                //afterCompletion 事务完成
//                // 调用父类的事务提交方法,空方法
//                //   super.afterCompletion(status);
//
//                //事务完成有可能是 回滚
////                int STATUS_COMMITTED = 0;
////                int STATUS_ROLLED_BACK = 1;
////                int STATUS_UNKNOWN = 2;
//                if (status == 0) {
//                    sentAsync(mqMessage);
//                    int m=0;
//                }
//
//            }

            @Override
            public void afterCommit() {
                System.out.println("send email after transaction commit...");
                sentAsync(mqMessage);
            }
        });

    }

    private void sentAsync(MqMessage mqMessage) {
        if (mqMessage == null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            //主线程无法捕捉子线程抛出的异常，除非设置捕捉 Thread.setDefaultUncaughtExceptionHandler
            try {
                rabbitMQTest.produceTest(mqMessage);
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }
}
