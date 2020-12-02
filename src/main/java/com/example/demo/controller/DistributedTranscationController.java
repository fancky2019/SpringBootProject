package com.example.demo.controller;

/**
 * 分布式事务
 * 1）开源框架LCN、EasyTrancation。处于性能能考虑最终不采用开源框架。
 * 2）采用消息中间件(RabbitMQ、Kafka)。
 * 设计方案：
 * 1、MicroServiceA 事务commit。
 * 2、发送MicroServiceB要操作的信息到RabbitMQ。关于步骤2为了保证能够正确生产，采用RabbitMQ的生产确认机制。
 * 如果还担心可以消息写入Redis,再写入RabbitMQ确保生产成功，RabbitMQ生产成功之后从Redis中移除。也可以在
 * MicroServiceB消费成功时候从Redis中移除。
 * <p>
 * <p>
 * 高可用设计方法：
 * A、MicroServiceA 入库完成，不提交事务。
 * B、写入Redis。
 * C、MicroServiceA提交事务。失败删除Redis数据。
 * D、写入RabbitMQ。确保正确生产。（可删除Redis数据，后续直接分析RabbitMQ失败队列。）
 * E、MicroServiceB从RabbitMQ中消费。Rabbit设置重试队列、失败队列等一系列措施确保正确消费。消费完成删除Redis数据。
 * F、分析RabbitMQ失败队列。（开启一个守护线程，轮询Redis中未被消费的数据。轮询间隔大于此条消息被消费的耗时（Interval>ConsumingTime）。
 * 轮询出那些数据状态为MicroServiceBUnSuccess的数据。）分析原因解决Bug或采取人工补救措施。
 * 注：E步骤数据操作，可在Redis数据中加一个状态字段(Status)，
 * 可以跟踪到具体哪个环节出了问题如下：
 * Enum
 * {
 * MicroServiceA,//MicroServiceA提交事务
 * RabbitMQ,//写入RabbitMQ
 * MicroServiceBUnSuccess,//MicroServiceB已经消费过但是没有消费成功。（方便后续问题分析）
 * MicroServiceBSuccess//此值可以不要，此时Redis数据已经删除，如果入数据库保存记录可以要（没必要）。
 * }
 * //此枚举设计类似事务中A、B账户扣款。A扣款之后更新Status=A，B账户更新成功之后Status=B，完成。
 * EnumAB
 * {
 * A,//A账户扣款
 * B//B账户扣款，此时完成。
 * }
 * <p>
 * F步骤可在Redis数据中加一个入库Redis的时间字段(DateTime)，在轮询的时候可以根据时间统计分析失败的时间分布情况。
 */
public class DistributedTranscationController {
}
