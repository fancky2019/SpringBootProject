package com.example.demo.rabbitMQ;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.support.BatchingStrategy;
import org.springframework.amqp.rabbit.core.support.SimpleBatchingStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 声明RabbitMQ的交换机、队列、并将相应的队列、交换机、RoutingKey绑定。
 */
@Configuration
public class RabbitMQConfig {

    //region 常量参数

    //region batch
    public static final String BATCH_DIRECT_EXCHANGE_NAME = "BatchSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String BATCH_DIRECT_ROUTING_KEY = "BatchRoutingKeySpringBoot";
    public static final String BATCH_DIRECT_QUEUE_NAME = "BatchQueueSpringBoot";
    //endregion


    //region DIRECT
    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot";
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";
    //endregion

    //region DIRECT
    public static final String DEAD_DIRECT_EXCHANGE_NAME = "DeadDirectExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DEAD_DIRECT_ROUTING_KEY = "DeadDirectExchangeRoutingKeySpringBoot";
    public static final String DEAD_DIRECT_QUEUE_NAME = "DeadDirectExchangeQueueSpringBoot";
    //endregion

    //region TOPIC
    public static final String TOPIC_EXCHANGE_NAME = "TopicExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String TOPIC_ROUTING_KEY = "TopicExchangeRoutingKeySpringBoot.*";
    public static final String TOPIC_QUEUE_NAME = "TopicExchangeQueueSpringBoot";
    public static final String TOPIC_ROUTING_KEY1 = "TopicExchangeRoutingKeySpringBoot1.#";
    public static final String TOPIC_QUEUE_NAME1 = "TopicExchangeQueueSpringBoot1";
    //endregion

    //region FANOUT
    public static final String FANOUT_EXCHANGE_NAME = "FanoutExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String FANOUT_ROUTING_KEY = "FanoutExchangeRoutingKeySpringBoot.*";
    public static final String FANOUT_QUEUE_NAME = "FanoutExchangeQueueSpringBoot";
    public static final String FANOUT_ROUTING_KEY1 = "FanoutExchangeRoutingKeySpringBoot1.#";
    public static final String FANOUT_QUEUE_NAME1 = "FanoutExchangeQueueSpringBoot1";
    //endregion

    //endregion

//    @Autowired
//    private ConnectionFactory connectionFactory;

    //@Bean注解的方法的参数可以任意加，反射会自动添加对应参数
    @Bean
    public RabbitTemplate RabbitTemplate(ConnectionFactory connectionFactory) {
        //公平分发模式在Spring-amqp中是默认的
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 消息返回, yml需要配置 publisher-returns: true
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("消息失败返回成功 ");
        });
        // 消息确认, yml需要配置 publisher-confirms: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送到交换机成功！ ");
            } else {
                System.out.println("消息发送到交换机失败！ ");
            }
        });

        return rabbitTemplate;
    }

//    @Bean
//    public BatchingRabbitTemplate batchingRabbitTemplate(ConnectionFactory connectionFactory) {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(1);
//        scheduler.initialize();
//        SimpleBatchingStrategy batchingStrategy= new SimpleBatchingStrategy(20, Integer.MAX_VALUE, 50);
//        BatchingRabbitTemplate batchingRabbitTemplate = new BatchingRabbitTemplate(batchingStrategy,scheduler );
//        batchingRabbitTemplate.setConnectionFactory(connectionFactory);
//
//        // 消息返回, yml需要配置 publisher-returns: true
//        batchingRabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
//            System.out.println("消息失败返回成功 ");
//        });
//        // 消息确认, yml需要配置 publisher-confirms: true
//        batchingRabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                System.out.println("消息批量发送到交换机成功！ ");
//            } else {
//                System.out.println("消息批量发送到交换机失败！ ");
//            }
//        });
//        return batchingRabbitTemplate;
//    }


    //批量 异步
    //    private BatchingRabbitTemplate batchingRabbitTemplate;
//    @Autowired
//    private  AsyncRabbitTemplate asyncRabbitTemplate;


    //region 配置交换机、队列、RoutingKey

//    @Bean("batchExchange")
//    public DirectExchange batchExchange() {
//        DirectExchange directExchange = new DirectExchange(BATCH_DIRECT_EXCHANGE_NAME);
//        return directExchange;
//    }
//
//    @Bean("batchQueue")
//    public Queue batchQueue() {
//        Queue queue = new Queue(BATCH_DIRECT_QUEUE_NAME);
//        return queue;
//    }
//
//    /**
//     * 绑定队列、交换机、路由Key
//     */
//    @Bean("bindingBatch")
//    public Binding bindingBatch() {
//        Binding binding = BindingBuilder.bind(batchQueue()).to(batchExchange()).with(BATCH_DIRECT_ROUTING_KEY);
//        return binding;
//    }
    //endregion

    //region DeadDirect

    //    @Bean("deadLetterExchange")
    @Bean("deadDirectExchange")
    public DirectExchange deadDirectExchange() {
        DirectExchange directExchange = new DirectExchange(DEAD_DIRECT_EXCHANGE_NAME);
        return directExchange;
    }

    @Bean("deadDirectQueue")
    public Queue deadDirectQueue() {
        Queue queue = new Queue(DEAD_DIRECT_QUEUE_NAME);
        return queue;
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean("bindingDeadDirect")
    public Binding bindingDeadDirect() {
        Binding binding = BindingBuilder.bind(deadDirectQueue()).to(deadDirectExchange()).with(DEAD_DIRECT_ROUTING_KEY);
        return binding;
    }
    //endregion

    //region Direct
    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE_NAME);
        return directExchange;
    }

    @Bean
    public Queue directQueue() {

        //设置死信队列的参数（交换机、路由key）
        // Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        HashMap<String, Object> args = new HashMap<>();
//        args.put("x-message-ttl", 30000);
        // 设置该Queue的死信的信箱
        args.put("x-dead-letter-exchange", DEAD_DIRECT_EXCHANGE_NAME);
        // 设置死信routingKey
        args.put("x-dead-letter-routing-key", DEAD_DIRECT_ROUTING_KEY);
//        QueueBuilder.durable(DIRECT_QUEUE_NAME).withArguments(args).build();
        return new Queue(DIRECT_QUEUE_NAME, true, false, false, args);
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindingDirect() {
        Binding binding = BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
        return binding;
    }
    //endregion


    //region Topic
    @Bean
    public TopicExchange topicExchange() {
        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME);
        return topicExchange;
    }

    @Bean
    public Queue topicQueue() {
        Queue queue = new Queue(TOPIC_QUEUE_NAME);
        return queue;
    }

    @Bean
    public Queue topicQueue1() {
        Queue queue = new Queue(TOPIC_QUEUE_NAME1);
        return queue;
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindingTopic() {
        Binding binding = BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TOPIC_ROUTING_KEY);//binding key
        return binding;
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindingTopic1() {
        Binding binding = BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_ROUTING_KEY1);//binding key
        return binding;
    }
    //endregion

    //region Fanout
    @Bean
    public Queue fanoutQueue() {
        Queue queue = new Queue(FANOUT_QUEUE_NAME);
        return queue;
    }

    @Bean
    public Queue fanoutQueue1() {
        Queue queue = new Queue(FANOUT_QUEUE_NAME1);
        return queue;
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        FanoutExchange fanoutExchange = new FanoutExchange(FANOUT_EXCHANGE_NAME);
        return fanoutExchange;
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindFanout() {
        Binding binding = BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
        return binding;
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindFanout1() {
        Binding binding = BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
        return binding;
    }
    //endregion

    //endregion
}
