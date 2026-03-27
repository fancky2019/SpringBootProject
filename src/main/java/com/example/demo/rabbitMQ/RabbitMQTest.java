package com.example.demo.rabbitMQ;

import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.rabbitMQ.producer.DirectExchangeProducer;
import com.example.demo.rabbitMQ.producer.FanoutExchangeProducer;
import com.example.demo.rabbitMQ.producer.TopicExchangeProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 源码参见github:https://github.com/spring-projects/spring-amqp
 *
 * spring cloud 消息总线默认集成了rabbitmq和kafka，使用上面和springboot没有区别
 *
 * 分布式事务设计：本地消息表
 * 1、将mq消息插入本息系统中和业务数据放在一个事务中提交从而保证原子性。
 * 2、启动一个定时任务扫描消息表获取为发送到mq的消息
 * 3、将本地消息表获取的数据循环发送到mq并更新本地数据库，发送状态为已发送
 * 4、mq 根据消息id 判断时候重复消费。
 *
 *
 *
 * 事务执行成功之后，启动一个线程异步执行发送到mq,这样避免等待一个cron 周期
 * volatile consumerCount=0;
 * concurrentHashMap<uuid,list> mapMsgs;
 * sysc send(concurrentHashMap msgs)
 * msgList中包含 msgs 的信息如果msgs中的状态为已发送,就从msgs中移除。
 * 发送到mq 代码。
 * 在mq 生产成功的回调中更新db,同事更新内存中的msg 状态为已发送,由于单例可能造成消息为收到生产成功的回调
 * 而下一个cron 调用就调用。但是可以设计while(true) sleep 1;最大等待3秒，等待所有生产确认，不然就释放锁。
 * if(consumerCount==msgs.cout) break;
 * consumerCount=0;
 * this.mapMsgs=msgs;
 * <
 * 这样会造成已经发送到mq 更新db 失败，重复投递的情况，所以mq要判断重复消费。
 *
 *
 *
 * 重复消费的msgId在redis中的过期时间设置1month
 *
 * 单活模式队列：
 * 单活模式队列：微服务分布式集群，多个生产者写一个队列，多个消费者只有一个消费者消费队列
 * x-single-active-consumer：默认false,单活模式，表示是否最多只允许一个消费者消费，如果有多个消费者同时绑定，
 * 则只会激活第一个，除非第一个消费者被取消或者死亡，才会自动转到下一个消费者。
 * 消息积压：将一个大队列分成几个小队列，根据messageId生产到相应队列，这样再配置单活队列，提高了消费能力，降低消息积压。
 *
 *
 * 可用性：rabbitmq 生产者默认异步发送，暂时没找到同步发送。不像rocketmq和kafka 支生产者持同步发送异步发送
 *  rabbitmq默认消息、队列、交换机都是持久化：
 *  发送时候指定消息持久化（deliveryMode=2）、
 *  声明队列时持久化（durable字段设置为true）、
 *  声明交换机时持久化（durable字段设置为true）
 *
 * 消息默认持久化（deliveryMode=2：MessageProperties 默认 DEFAULT_DELIVERY_MODE = MessageDeliveryMode.PERSISTENT;）、
 * 队列默认持久化true、交换机默认持久化true
 *
 *
 *
 *
 * RabbitMQ 的消费模式分为两种：推模式和拉模式。
 *
 * 推模式（Push）：消息中间件主动将消息推送给消费者，推模式采用 Basic.Consume 进行消费。
 * 拉模式（Pull）：消费者主动从消息中间件拉取消息，拉模式则是调用 Basic.Get 进行消费。
 *
 *RabbitMQ 3.8+ 推荐使用 仲裁队列（Quorum Queue） 替代镜像队列（Mirrored Queue），它基于 Raft 算法，减少存储和同步开销。
 *channel.queue_declare(queue='my_quorum_queue', arguments={'x-queue-type': 'quorum'})
 * 但是还是解决不了水平扩容问题
 *
 *分片队列： 不支持 HA，某个分片丢失后不可恢复。RabbitMQ 3.8+ 推荐使用 Quorum Queue 替代镜像队列
 * RabbitMQ Sharding Plugin 是最简单的方式，推荐使用。
 * 手动分片 适用于不支持插件的 RabbitMQ 版本。
 * 分片队列 vs. 镜像队列：
 * 分片队列 提高吞吐量，但不保证高可用。
 * 镜像队列 适合高可用场景，但吞吐量较低
 *
 *
 *
 *
 * RabbitMQ 默认的消费超时时间为 30 分钟（1800000 毫秒）。如果消费者在这段时间内未对消息进行确认（ACK），RabbitMQ 会关闭该 Channel，并抛出 PRECONDITION_FAILED 异常，导致后续消息无法继续消费
 *
 *永久调整（需修改配置文件 rabbitmq.conf）： 参见word 文档
 * consumer_timeout = 36000000  # 单位：毫秒
 *
 *
 *
 * rabbitmq 队列之间是多线程消费，队列内是单线程
 * 多线程消费：多个队列可以被不同的消费者同时消费
 * 单线程消费：单个队列内的消息按顺序被消费（默认情况下）
 *
 *
 * durable=true（队列持久化）：仅保证队列的元数据在RabbitMQ重启后不丢失。
 *
 * delivery_mode=2（消息持久化）：才决定消息内容是否写入磁盘。
 *
 * 确保消息不丢失：
 * 消息默认存内存（非持久化时），持久化消息会异步写入磁盘
 *
 * 不是每次写消息都立即落盘，RabbitMQ有优化策略
 *
 * 即使设置了durable和delivery_mode=2，消息仍可能丢失（在崩溃和刷盘间隙）
 *
 * 要确保消息不丢失，需要：持久化队列 + 持久化消息 + 发布者确认 + 手动确认 + 镜像队列的完整方案
 *
 *
 *
 * Sleuth
 *
 * 1、发送端：Sleuth 拦截 RabbitTemplate，将当前 traceId 写入 AMQP 消息头（如 X-B3-TraceId）。
 *    Sleuth 会拦截所有通过 RabbitTemplate 发送的消息，自动添加标准的 B3 传播头信息。
 * 2、传输：消息携带头信息在 RabbitMQ 中流转。
 *
 * 3、接收端：Sleuth 的 MessageListener 拦截消息，读取头信息并恢复 traceId 到当前线程。
 * 在消费端，Sleuth 会自动从消息头中解析出 traceId 和 spanId，并恢复到当前的日志上下文中（MDC）
 *
 *
 *
 * 拦截 RabbitTemplate :BeanPostProcessor 替换为jdk动态代理的bean到容器中
 *
 * import org.springframework.amqp.rabbit.core.RabbitTemplate;
 * import org.springframework.beans.BeansException;
 * import org.springframework.beans.factory.config.BeanPostProcessor;
 * import org.springframework.cloud.sleuth.Tracer;
 * import org.springframework.stereotype.Component;
 * import java.lang.reflect.InvocationHandler;
 * import java.lang.reflect.Method;
 * import java.lang.reflect.Proxy;
 *
 * @Component
 * public class RabbitTemplateInterceptor implements BeanPostProcessor {
 *
 *     private final Tracer tracer;
 *
 *     public RabbitTemplateInterceptor(Tracer tracer) {
 *         this.tracer = tracer;
 *     }
 *
 *     @Override
 *     public Object postProcessAfterInitialization(Object bean, String beanName)
 *             throws BeansException {
 *
 *         if (bean instanceof RabbitTemplate) {
 *             return proxyRabbitTemplate((RabbitTemplate) bean);
 *         }
 *         return bean;
 *     }
 *
 *
 * //使用装饰器模式会更好，代理模式倾向于访问控制。装饰器功能增强
 *     private Object proxyRabbitTemplate(RabbitTemplate rabbitTemplate) {
 *         return Proxy.newProxyInstance(
 *             rabbitTemplate.getClass().getClassLoader(),
 *             rabbitTemplate.getClass().getInterfaces(),
 *             new InvocationHandler() {
 *                 @Override
 *                 public Object invoke(Object proxy, Method method, Object[] args)
 *                         throws Throwable {
 *
 *                     // 拦截 send 和 convertAndSend 方法
 *                     if (method.getName().equals("send") ||
 *                         method.getName().equals("convertAndSend")) {
 *
 *                         // 添加自定义逻辑
 *                         if (tracer.currentSpan() != null) {
 *                             String traceId = tracer.currentSpan().context().traceId();
 *                             System.out.println("发送消息 traceId: " + traceId);
 *
 *                             // 如果有 Message 参数，添加 header
 *                             for (Object arg : args) {
 *                                 if (arg instanceof Message) {
 *                                     ((Message) arg).getMessageProperties()
 *                                         .setHeader("intercepted-trace-id", traceId);
 *                                 }
 *                             }
 *                         }
 *                     }
 *
 *                     return method.invoke(rabbitTemplate, args);
 *                 }
 *             }
 *         );
 *     }
 * }
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
操作	           方法	                          行为	              是否会重新消费
确认	           basicAck	                      消息从队列移除	       否
拒绝-不重入	   basicNack(requeue=false)	      消息被丢弃或进入死信	   否
拒绝-重入	   basicNack(requeue=true)	      消息重新回到队首	       是，立即重试
不确认不拒绝	   什么都不做	                      消息保持 unacked	   不会，但会阻塞队列


业务异常导致不确认也不拒绝
不确认也不拒绝:消息会在队列中一直处于 Unacked 状态，不会被重新投递，也不会消失，造成消息堆积。



场景              	Unacked消息影响	               后续消息处理
单消费者，prefetch=1	完全阻塞	                       所有后续消息停止消费 ❌
单消费者，prefetch>1	部分阻塞	                       已预取消息继续处理，新消息不接收 ⚠️
多消费者，prefetch=1	单个消费者阻塞	其                  他消费者正常处理 ✅
多消费者，prefetch>1	单个消费者部分阻塞	                其他消费者正常处理，阻塞消费者无法接收新消息 ⚠️

关键要点：
Unacked 消息会占用消费者的 prefetch 配额
只有配额释放后，消费者才能接收新消息
其他消费者不受影响，可以继续消费
必须确保每条消息最终都会被 Ack 或 Nack
建议使用 try-finally 确保一定会确认或拒绝

total=Ready+Unacked;

生产者发布消息
↓
[Ready] ←────┐
↓         │
消费者获取     │
↓         │    消费者断开连接
[Unacked]    │ (或异常退出)
↓         │
消费者确认     │
↓         │
消息删除      ┘
 */




@Component
public class RabbitMQTest {
    private static Logger logger = LogManager.getLogger(RabbitMQTest.class);
    @Autowired
    private DirectExchangeProducer directExchangeProducer;
    @Autowired
    private FanoutExchangeProducer fanoutExchangeProducer;
    @Autowired
    private TopicExchangeProducer topicExchangeProducer;

    public void produceTest() {
        //DEMO  链接：http://www.rabbitmq.com/getstarted.html
        //NuGet添加RabbitMQ.Client引用
        //RabbitMQ UI管理:http://localhost:15672/   账号:guest 密码:guest
        //先启动订阅，然后启动发布
        //var factory = new ConnectionFactory(){ HostName = "192.168.1.121", Port = 5672 }; //HostName = "localhost",
        //用下面的实例化，不然报 None of the specified endpoints were reachable
        //var factory = new ConnectionFactory() { HostName = "192.168.1.121", Port = 5672, UserName = "fancky", Password = "123456" };

        //http://www.rabbitmq.com/tutorials/tutorial-three-dotnet.html


        // 公平分发模式在Spring-amqp中是默认的

        try {

//            directExchangeProducer.produceDelayedMessage();
//            directExchangeProducer.producer();
            directExchangeProducer.standardTest();
//            directExchangeProducer.produceNotConvertSent();
//            directExchangeProducer.publishInBatch();

//        fanoutExchangeProducer.producer();
//            topicExchangeProducer.producer();


//            directExchangeProducer.publishInBatch();

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void produceTest(MqMessage mqMessage) {
        directExchangeProducer.produceNotConvertSent(mqMessage);
    }

}
