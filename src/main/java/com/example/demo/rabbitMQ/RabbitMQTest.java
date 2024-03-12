package com.example.demo.rabbitMQ;

import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.rabbitMQ.consumer.DirectExchangeConsumer;
import com.example.demo.rabbitMQ.producer.DirectExchangeProducer;
import com.example.demo.rabbitMQ.producer.FanoutExchangeProducer;
import com.example.demo.rabbitMQ.producer.TopicExchangeProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 源码参见github:https://github.com/spring-projects/spring-amqp
 * <p>
 * spring cloud 消息总线默认集成了rabbitmq和kafka，使用上面和springboot没有区别
 * <p>
 * 分布式事务设计：1、将没钱消息插入本息系统中和业务数据放在一个事务中提交从而保证原子性。
 * 2、启动一个定时任务扫描消息表获取为发送到mq的消息
 * 3、将本地消息表获取的数据循环发送到mq并更新本地数据库，发送状态为已发送
 * 4、mq 根据消息id 判断时候重复消费。
 * <p>
 * <p>
 * <p>
 * 事务执行成功之后，启动一个线程异步执行发送到mq,这样避免等待一个cron 周期
 * volatile cnsumerCount=0;
 * concurrentHashMap<uuid,list> mapMsgs;
 * sysc send(concurrentHashMap msgs)
 * msgList中包含 msgs 的信息如果msgs中的状态为已发送,就从msgs中移除。
 * 发送到mq 代码。
 * 在mq 生产成功的回调中更新db,同事更新内存中的msg 状态为已发送,由于单例可能造成消息为收到生产成功的回调
 * 而下一个cron 调用就调用。但是可以设计while(true) sleep 1;最大等待3秒，等待所有生产确认，不然就释放锁。
 * if(consumerCount==msgs.cout) break;
 * consumerCount=0;
 * this.mapMsgs=msgs;
 * <p>
 * 这样会造成已经发送到mq 更新db 失败，重复投递的情况，所以mq要判断重复消费。
 * <p>
 * <p>
 * <p>
 * 重复消费的msgId在redis中的过期时间设置1month
 * <p>
 * 单活模式队列：
 * 单活模式队列：微服务分布式集群，多个生产者写一个队列，多个消费者只有一个消费者消费队列
 * x-single-active-consumer：默认false,单活模式，表示是否最多只允许一个消费者消费，如果有多个消费者同时绑定，
 * 则只会激活第一个，除非第一个消费者被取消或者死亡，才会自动转到下一个消费者。
 * 消息积压：将一个大队列分成几个小队列，根据messageId生产到相应队列，这样再配置单活队列，提高了消费能力，降低消息积压。
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

    public void test() {
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

    public void test(MqMessage mqMessage) {
        directExchangeProducer.produceNotConvertSent(mqMessage);
    }

}
