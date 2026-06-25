package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 *
 *NameServer :服务注册与发现、路由信息存储：
 * Broker 启动时：集群中的每个 Broker 节点（Master 或 Slave）在启动时，都会向所有的 NameServer 进行注册，报告自己的 IP 地址、端口号和它存储的 Topic 信息。
 *
 * NameServer  “路由表”  topic 对应的broker
 *
 *
 *Broker  负责真实的消息存储、转发和查询。
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
 *1、Broker 注册
 * Broker 启动时会主动向 NameServer 注册自己：
 * 自己的 IP / 端口
 * 属于哪个 Topic
 * 队列信息（Queue 数量等）
 * NameServer 里就有一份：
 * Topic → Broker 的映射表（路由表）
 *
 *
 * 2、给 Producer / Consumer 提供路由信息
 * Producer / Consumer 启动时：
 * 先连 NameServer
 * 查询：
 * “这个 Topic 在哪些 Broker 上？”
 * 拿到路由信息后
 * 直接和 Broker 通信（不再经过 NameServer）
 *NameServer ≠ 消息中转
 *
 *3、心跳 & 剔除失效 Broker
 * Broker 定期向 NameServer 发心跳
 * 如果：
 * 心跳超时
 * Broker 挂了
 *
 * NameServer 会把它从路由表里剔除
 *
 *
 *
 *    NameServer作用类似Zookeeper
 *     broker  负责消息存储
 *
 *     broker负责接收并存储消息,发送消息指定topic,订阅也指定topic 相当于rabbitmq的queue.rabbitmq多了个exchange的概念，消息路由
 *
 *     参见项目rocketmq demo
 *
 *      //操作界面
 *      nameSrvAddr=127.0.0.1:7080
 *
 *      消息可靠性：
 *      一、生成消息：1、同步发送，2、异步发送，回调确认机制
 *      二、broker 1、同步刷盘，将操作系统pageCache中的数据刷新到磁盘，2、主从模式，过半从同步
 *                  ## 默认情况为 ASYNC_FLUSH
 *                  flushDiskType = SYNC_FLUSH
 *      三、消费者消费确认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS
 *
 *
 *
 *Topic、Broker、MessageQueue
 *
 * 一个 Topic = 多个队列（MessageQueue）
 * 队列分布在不同 Broker 上
 *
 *
 *
 *
 * 选主：
 * 自动选主模式：必须显式开启
 * 要想实现自动选主，无论是使用较早的 DLedger 模式还是更新的 Controller 模式，都必须显式开启对应的配置，这不是默认行为。
 *
 * DLedger 模式：需要在 Broker 的配置文件中，将 enableDLegerCommitLog 设置为 true，并配置 DLedger 相关的组和节点信息。
 *
 * Controller 模式（RocketMQ 5.0+）：这是目前官方推荐的新方案。需要在 Broker 端设置 enableControllerMode=true，并指定 Controller 的地址；同时在 NameServer 或独立的 Controller 组件中启用 Controller 功能。在这种模式下，你甚至不需要再手动指定 brokerId 和 brokerRole，这些角色会由 Controller 组件自动分配和协调。
 *
 *
 *5.0+ 官方推荐的 Controller 模式
 * # 关键：开启 Controller 模式
 * enableControllerMode = true
 * # 指向所有 Controller 的地址，多个用分号隔开
 * controllerAddr = 127.0.0.1:9877;127.0.0.1:9878;127.0.0.1:9879
 */
@Slf4j
@Component
public class RocketmqTest {


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
