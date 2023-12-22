package com.example.demo.rabbitMQ;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * rabbitMQ安装目录C:\Program Files\RabbitMQ Server\rabbitmq_server-3.10.5\sbin 下控制台执行命令
 * # 查看所有队列
 * rabbitmqctl list_queues
 * <p>
 * # 根据 queue_name 参数，删除对应的队列
 * rabbitmqctl delete_queue queue_name
 * <p>
 * <p>
 * 声明RabbitMQ的交换机、队列、并将相应的队列、交换机、RoutingKey绑定。
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    @Autowired
    ObjectMapper objectMapper;
    //    @Autowired
//    private DemoProductService demoProductService;
    //region 常量参数
    public static final int RETRY_INTERVAL = 100000;
    //region batch
    public static final String BATCH_DIRECT_EXCHANGE_NAME = "BatchSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String BATCH_DIRECT_ROUTING_KEY = "BatchRoutingKeySpringBoot";
    public static final String BATCH_DIRECT_QUEUE_NAME = "BatchQueueSpringBoot";

    public static final String BATCH_DIRECT_ROUTING_KEY_DLX = "BatchRoutingKeySpringBootDlx";
    public static final String BATCH_DIRECT_QUEUE_NAME_DLX = "BatchQueueSpringBootDlx";
    //endregion


    //region DIRECT
    public static final String DIRECT_EXCHANGE = "DirectExchangeSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot";
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";

    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DIRECT_ROUTING_KEY_DLX = "directRoutingKeyDlx";
    public static final String DIRECT_QUEUE_DLX = "DirectExchangeQueueSpringBootDlx";
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


    //region  rabbitmq_delayed_message_exchange
    public static final String DELAYED_MESSAGE_EXCHANGE = "DelayedMessageSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DELAYED_MESSAGE_KEY = "DelayedMessageRoutingKeySpringBoot";
    public static final String DELAYED_MESSAGE_QUEUE = "DelayedMessageQueueSpringBoot";
    //endregion


    //endregion

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类.默认为true
//        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }


//    //发送消息时如不配置序列化方法则按照java默认序列化机制，则会造成发送编码不符合
//    @Bean
//    public MessageConverter messageConverter(){
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        om.registerModule(new JavaTimeModule());
//        return new Jackson2JsonMessageConverter(om);
//
//    }


//    @Autowired
//    private ConnectionFactory connectionFactory;

    //@Bean注解的方法的参数可以任意加，反射会自动添加对应参数
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        //公平分发模式在Spring-amqp中是默认的
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);//新版加此句

        //json 序列化，默认SimpleMessageConverter jdk 序列化,需要配置objectMapper，
        // 默认objectMapper LocalDateTime列化有问题，可以在字段上配置JsonDeserialize 参见RabbitMqMessage
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter(this.objectMapper));


        // 消息生产到交换机没有路由到队列 消息返回, yml需要配置 publisher-returns: true
        // 新版 #发布确认 publisher-confirms已经修改为publisher-confirm-type，
//        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
//
//            int m=0;
////            RabbitMqMessage
//            //            System.out.println("消息生产到交换机没有路由到队列");
////            log.info("消息 - {} 路由到队列失败！", msgId);
//        });

//////        //比上面的方法多一个s是Returns不是Return
////        //ReturnedMessage  //不行
        rabbitTemplate.setReturnsCallback(returnedMessage ->
        {
            String exchange = returnedMessage.getExchange();
            String routingKey = returnedMessage.getRoutingKey();
            int replyCod = returnedMessage.getReplyCode();
            String replyText = returnedMessage.getReplyText();
            String messageId = "";
            RabbitMqMessage rabbitMqMessage = null;

            // json 序列化，默认SimpleMessageConverter jdk 序列化
            try {

                String failedMessage = new String(returnedMessage.getMessage().getBody());
                rabbitMqMessage = objectMapper.readValue(failedMessage, RabbitMqMessage.class);
                messageId = rabbitMqMessage.getMessageId();
            } catch (Exception e) {
                log.info("", e);
            }

//            // 默认jdk 序列化：SimpleMessageConverter  序列化  rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(returnedMessage.getMessage().getBody()))) {
//                rabbitMqMessage = (RabbitMqMessage) ois.readObject();
//
//            } catch (Exception e) {
//                log.error("", e);
//            }

            messageId = rabbitMqMessage.getMessageId();

            log.info("消息 - {} 路由到队列失败.", messageId);
        });

//        CachingConnectionFactory.ConfirmType


//        NONE ，禁用发布确认模式，是默认值。
//
//        CORRELATED，发布消息时会携带一个CorrelationData，被ack/nack时CorrelationData会被返回进行对照处理，CorrelationData可以包含比较丰富的元信息进行回调逻辑的处理。
//
//        SIMPLE，当被ack/nack后会等待所有消息被发布，如果超时会触发异常，甚至关闭连接通道。

        //当消息路由失败时候先执行  setConfirmCallback, setReturnCallback后执行

        //消息没有生产到交换机
//        // 消息生产确认, yml需要配置 publisher-confirms: true
        rabbitTemplate.setConfirmCallback(new PushConfirmCallback());

        return rabbitTemplate;
    }


    //json 序列化，默认SimpleMessageConverter jdk 序列化
    //配置RabbitTemplate和RabbitListenerContainerFactory
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new Jackson2JsonMessageConverter(this.objectMapper));
        return factory;
    }


    /*
    多线程消费:涉及到消费顺序行要将一个大队列根据业务消息id分成多个小队列
    配置文件为默认的SimpleRabbitListenerContainerFactory 配置
    该配置为具体的listener 指定SimpleRabbitListenerContainerFactory
     */
    @Bean("multiplyThreadContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(5);  //设置并发消费线程数
        factory.setMaxConcurrentConsumers(5); //最大发消费线程数
//        消息状态：ready:准备发送给消费之
//        unacked:发送给消费者消费还没有ack
//        total：总消息数量=ready+unacked
        //每次预取10条信息放在线程的消费队列里，该线程还是1条一条从从该线程的缓冲队列里取消费。直到
        //缓冲队列里的消息消费完，再从mq的队列里取。
        // 调试可到mq插件查看 ready unacked 消息数量，打印消费者消费线程的消息id
        factory.setPrefetchCount(10);
        // 是否重回队列
//        factory.setDefaultRequeueRejected(true);
        // 手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);

//        //json 序列化，默认SimpleMessageConverter jdk 序列化
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }


    //region batch

    //批量 异步
    //    private BatchingRabbitTemplate batchingRabbitTemplate;
//    @Autowired
//    private  AsyncRabbitTemplate asyncRabbitTemplate;

    @Bean("batchQueueRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory batchQueueRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //设置批量
        factory.setBatchListener(true);
        factory.setConsumerBatchEnabled(true);//设置BatchMessageListener生效
        factory.setBatchSize(5);//设置监听器一次批量处理的消息数量 5x批量合并的消息=一次消费的数量
        return factory;
    }

    /*
     //如果其中一条消费失败nack会有问题，ack其中一条会有问题，要么整批nack,不采用消息合并生产
     */

    @Bean
    public BatchingRabbitTemplate batchingRabbitTemplate(ConnectionFactory connectionFactory) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.initialize();

        // 一次批量的数量：spring 发送将多条合并成一条
        //如果其中一条消费失败nack会有问题，不采用消息合并生产
        int batchSize = 7;
        SimpleBatchingStrategy batchingStrategy = new SimpleBatchingStrategy(batchSize, Integer.MAX_VALUE, 500);
        BatchingRabbitTemplate batchingRabbitTemplate = new BatchingRabbitTemplate(batchingStrategy, scheduler);
        batchingRabbitTemplate.setConnectionFactory(connectionFactory);

        // 消息返回, yml需要配置 publisher-returns: true
        batchingRabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("消息生产到交换机没有路由到队列 ");
        });
        // 消息确认, yml需要配置 publisher-confirms: true
        batchingRabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息批量发送到交换机成功！ ");
            } else {
                System.out.println("消息批量发送到交换机失败！ ");
            }
        });
        return batchingRabbitTemplate;
    }

    @Bean("batchExchange")
    public DirectExchange batchExchange() {
        DirectExchange directExchange = new DirectExchange(BATCH_DIRECT_EXCHANGE_NAME);
        return directExchange;
    }

    @Bean("batchQueue")
    public Queue batchQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", BATCH_DIRECT_EXCHANGE_NAME);
        map.put("x-dead-letter-routing-key", BATCH_DIRECT_ROUTING_KEY_DLX);
        //单活队列
//        map.put("x-single-active-consumer", true);
//        HashMap<String,Object> args = new HashMap<String,Object>();
//        args.put("x-single-active-consumer", true);
//       //创建Queue
//        channel.queueDeclare(queueName, true, false, false, args);
        return new Queue(BATCH_DIRECT_QUEUE_NAME, true, false, false, map);

    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean("bindingBatch")
    public Binding bindingBatch() {
        Binding binding = BindingBuilder.bind(batchQueue()).to(batchExchange()).with(BATCH_DIRECT_ROUTING_KEY);
        return binding;
    }

    @Bean("batchQueueDlx")
    public Queue batchQueueDlx() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-message-ttl", RETRY_INTERVAL);
        map.put("x-dead-letter-exchange", BATCH_DIRECT_EXCHANGE_NAME);
        map.put("x-dead-letter-routing-key", BATCH_DIRECT_ROUTING_KEY);
        return new Queue(BATCH_DIRECT_QUEUE_NAME_DLX, true, false, false, map);

    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean("bindingBatchDlx")
    public Binding bindingBatchDlx() {
        Binding binding = BindingBuilder.bind(batchQueueDlx()).to(batchExchange()).with(BATCH_DIRECT_ROUTING_KEY_DLX);
        return binding;
    }
    //endregion

    //region DeadDirect

    @Bean("deadDirectQueue")
    public Queue deadDirectQueue() {


        /*
        exclusive
        只对首次声明它的连接（Connection）可见
        会在其连接断开的时候自动删除。
         */
        /*
        (String name, boolean durable, boolean exclusive, boolean autoDelete)
           this(name, true, false, false);
         */
        Map<String, Object> map = new HashMap<>();
        map.put("x-message-ttl", RETRY_INTERVAL);
        map.put("x-dead-letter-exchange", DIRECT_EXCHANGE);
        map.put("x-dead-letter-routing-key", DIRECT_ROUTING_KEY);
        return new Queue(DIRECT_QUEUE_DLX, true, false, false, map);
//

    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean("bindingDeadDirect")
    public Binding bindingDeadDirect() {
        Binding binding = BindingBuilder.bind(deadDirectQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY_DLX);
        return binding;
    }
    //endregion

    //region Direct
    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE);
        return directExchange;
    }

    @Bean
    public Queue directQueue() {

        //设置死信队列的参数（交换机、路由key）
        // Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        HashMap<String, Object> args = new HashMap<>();
//        args.put("x-message-ttl", 30000);
        // 设置该Queue的死信的队列
        args.put("x-dead-letter-exchange", DIRECT_EXCHANGE);
        // 设置死信routingKey
        args.put("x-dead-letter-routing-key", DIRECT_ROUTING_KEY_DLX);
//        QueueBuilder.durable(DIRECT_QUEUE_NAME).withArguments(args).build();
        return new Queue(DIRECT_QUEUE_NAME, true, false, false, args);
//
//        return new Queue(DIRECT_QUEUE_NAME);

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


    //region DelayedMessage
    @Bean
    public DirectExchange directDelayedMessageExchange() {
        DirectExchange directExchange = new DirectExchange(DELAYED_MESSAGE_EXCHANGE);
        directExchange.setDelayed(true);
        return directExchange;
    }

    @Bean
    public Queue directDelayedMessageQueue() {

//        QueueBuilder.durable(DIRECT_QUEUE_NAME).withArguments(args).build();
        return new Queue(DELAYED_MESSAGE_QUEUE);
    }

    /**
     * 绑定队列、交换机、路由Key
     */
    @Bean
    public Binding bindingDirectDelayedMessage() {
        Binding binding = BindingBuilder.bind(directDelayedMessageQueue()).to(directDelayedMessageExchange()).with(DELAYED_MESSAGE_KEY);
        return binding;
    }
    //endregion


    //endregion
}
