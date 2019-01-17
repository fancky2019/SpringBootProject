package com.example.demo.rabbitMQ;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 声明RabbitMQ的交换机、队列、并将相应的队列、交换机、RoutingKey绑定。
 */
@Configuration
public class RabbitMQConfig {

    //region 常量参数

    //region DIRECT
    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot.Direct";
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";
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

    @Autowired
    private ConnectionFactory connectionFactory;


    @Bean
    public RabbitTemplate RabbitTemplate() {
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

    //region 配置交换机、队列、RoutingKey

    //region Direct
    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE_NAME);
        return directExchange;
    }

    @Bean
    public Queue directQueue() {
        Queue queue = new Queue(DIRECT_QUEUE_NAME);
        return queue;
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
