package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 *
 *
 *
 部署模式	                  最小节点数	特点	适用场景
 单 Master 	                  1	无冗余，风险高	本地测试、开发环境
 多 Master（无 Slave）	       	2+	高性能，无 Slave 冗余	高性能需求，允许少量消息丢失
 多 Master 多 Slave（异步复制）	4+ (2M2S)	Master-Slave 异步复制，主备延迟毫秒级	平衡性能与可用性
 多 Master 多 Slave（同步双写）	4+ (2M2S)	Master-Slave 同步双写，数据强一致	金融、支付等高可靠性场景
 RocketMQ-on-DLedger	        3+	基于 Raft 协议，自动选主，故障自愈	完全自动化的高可用架构
 *
 *
 *
 * RocketMQ-on-DLedger: 和redis的类似,可设置至少有多少个从同步才返回写入成功
 * 日志复制与数据同步
 * Leader 写入流程
 *
 * 消息先写入 Leader 的 CommitLog，同时转发给所有 Follower8。
 *
 * 等待多数派确认：Leader 收到过半节点的成功响应后，才确认消息提交8。
 *
 * 数据一致性修复
 *
 * COMPARE 模式：当 Follower 数据不一致时，Leader 发送对比请求，定位差异点8。
 *
 * TRUNCATE 操作：删除 Follower 的差异数据后，重新同步最新日志8。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Slf4j
@Component
public class RocketmqTest {

    /**
    NameServer作用类似Zookeeper
    broker  负责消息存储

    broker负责接收并存储消息,发送消息指定topic,订阅也指定topic 相当于rabbitmq的queue.rabbitmq多了个exchange的概念，消息路由

    参见项目rocketmq demo

     //操作界面
     nameSrvAddr=127.0.0.1:7080

     消息可靠性：
     一、生成消息：1、同步发送，2、异步发送，回调确认机制
     二、broker 1、同步刷盘，将操作系统pageCache中的数据刷新到磁盘，2、主从模式，过半从同步
                 ## 默认情况为 ASYNC_FLUSH
                 flushDiskType = SYNC_FLUSH
     三、消费者消费确认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS
     */

    @Autowired
    RocketMQProducer rocketMQProducer;
    public void test(String topic) {
//        CompletableFuture.runAsync(()->
//        {
        //不要开启线程调用否则子线程内的异常抛不出来
////            rocketMQProducer.send("rocketMqTest");
//            rocketMQProducer.sendMsg("rocketMqTest");
////            rocketMQProducer.sendAsyncMsg("rocketMqTest");
//        });


        if(StringUtils.isEmpty(topic))
        {
            rocketMQProducer.sendMsg("rocketMqTest");
        }else {
            rocketMQProducer.sendMsg("rocketMqTest",topic);
        }
    }
}
