package com.example.demo.rabbitMQ;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.service.demo.IMqMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.batch.SimpleBatchingStrategy;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * rabbitMQ安装目录C:\Program Files\RabbitMQ Server\rabbitmq_server-3.10.5\sbin 下控制台执行命令
 * # 查看所有队列
 * rabbitmqctl list_queues
 *
 * # 根据 queue_name 参数，删除对应的队列
 * rabbitmqctl delete_queue queue_name
 *
 *
 * 声明RabbitMQ的交换机、队列、并将相应的队列、交换机、RoutingKey绑定。
 *
 * 分区消费 = 并行消费 + 多线程
 * 从原队列中取出消息，按业务ID重新分发到多个分区队列。这是一个数据迁移的过程。
 *
 *
 *
 *
 *
 *
 */
@Configuration
@Slf4j
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirm-type:CORRELATED}")
    private String publisherConfirmType;

    @Value("${spring.rabbitmq.publisher-returns:true}")
    private boolean publisherReturns;

    /**
     * 获取集群地址配置
     * 可以从配置文件中读取 spring.rabbitmq.addresses
     */
    @Value("${spring.rabbitmq.addresses:}")
    private String clusterAddresses;


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PushConfirmCallback pushConfirmCallback;
    @Autowired
    private TaskExecutor rabbitMQThreadPoolExecutor;
    //    @Autowired
//    private DemoProductService demoProductService;
    //region 常量参数
    //  x-message-ttl 参数的单位是毫秒。
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
    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
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

//    /**
//     * 配置 ConnectionFactory
//     * 支持单节点和集群配置
//     */
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory factory;
//
//        // 检查是否配置了集群地址
//
//        if (StringUtils.isNotEmpty(this.clusterAddresses)) {
//            // 集群模式：使用 addresses 配置
//            factory = new CachingConnectionFactory();
//            factory.setAddresses(clusterAddresses);
//            log.info("RabbitMQ 集群模式，地址: {}", clusterAddresses);
//        } else {
//            // 单节点模式：使用 host + port
//            factory = new CachingConnectionFactory(host, port);
//            log.info("RabbitMQ 单节点模式，host: {}, port: {}", host, port);
//        }
//
//        // 基础配置
//        factory.setUsername(username);
//        factory.setPassword(password);
//        factory.setVirtualHost(virtualHost);
//
//        // 连接超时设置（毫秒）
//        factory.setConnectionTimeout(30000);
//
//        // 心跳超时（秒），建议设置为 60 秒
//        factory.setRequestedHeartBeat(60);
//
//        // Channel 缓存大小
//        factory.setChannelCacheSize(25);
//
//        // 连接缓存模式
//        factory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
//
//        // 添加连接监听器
//        factory.addConnectionListener(new RabbitMQConnectionListener());
//
//        // 添加 Channel 监听器（可选）
//        factory.addChannelListener(new RabbitMQChannelListener());
//
//        log.info("RabbitMQ ConnectionFactory 初始化完成");
//        return factory;
//    }


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

                //没有路由到队列的设置未生产成功
                IMqMessageService mqMessageService = applicationContext.getBean(IMqMessageService.class);
//                LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
//                updateWrapper.set(MqMessage::getStatus, 0);
//                updateWrapper.eq(MqMessage::getMsgId, messageId);//条件
//                mqMessageService.update(updateWrapper);

                LambdaQueryWrapper<MqMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(MqMessage::getMsgId, messageId);
                MqMessage mqMessage = mqMessageService.getOne(lambdaQueryWrapper);
                mqMessage.setStatus(0);
                mqMessageService.updateById(mqMessage);


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

        //生产者 → Exchange → (路由匹配) → Queue → 消费者
        //消息没有生产到交换机
//        // 消息生产确认, yml需要配置 publisher-confirms: true
        rabbitTemplate.setConfirmCallback(pushConfirmCallback);

        return rabbitTemplate;
    }


    //分区消费 = 并行消费 + 多线程
    //json 序列化，默认SimpleMessageConverter jdk 序列化
    //配置RabbitTemplate和RabbitListenerContainerFactory
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new Jackson2JsonMessageConverter(this.objectMapper));
        //覆盖配置文件的配置
//        单消费者，prefetch=1	完全阻塞	                       所有后续消息停止消费 ❌
//        单消费者，prefetch>1	部分阻塞	                       已预取消息继续处理，新消息不接收 ⚠️
//        多消费者，prefetch=1	单个消费者阻塞	其                  他消费者正常处理 ✅
//        多消费者，prefetch>1	单个消费者部分阻塞	                其他消费者正常处理，阻塞消费者无法接收新消息 ⚠️
        //1个ack 不成功会停止消费，后续消息无法消费
        factory.setPrefetchCount(1);
        // 必须配置！避免线程爆炸
        factory.setTaskExecutor(rabbitMQThreadPoolExecutor);
        return factory;
    }


    /**
     * 分区消费 = 并行消费 + 多线程
     * 从原队列中取出消息，按业务ID重新分发到多个分区队列。这是一个数据迁移的过程。
     多线程消费:涉及到消费顺序行要将一个大队列根据业务消息id分成多个小队列
     配置文件为默认的SimpleRabbitListenerContainerFactory 配置
     该配置为具体的listener 指定SimpleRabbitListenerContainerFactory
     */
    @Bean("multiplyThreadContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(10);  //设置并发消费者数量。RabbitMQ 会创建 10 个独立的消费者连接/线程
        factory.setMaxConcurrentConsumers(50); //最大发消费线程数,消息积压时候会动态扩到50个消费者
//        消息状态：ready:准备发送给消费之
//        unacked:发送给消费者消费还没有ack
//        total：总消息数量=ready+unacked
        //每次预取10条信息放在线程的消费队列里，该线程还是1条一条从从该线程的缓冲队列里取消费。直到
        //缓冲队列里的消息消费完，再从mq的队列里取。
        // 调试可到mq插件查看 ready unacked 消息数量，打印消费者消费线程的消息id
        //每个消费者一次可以预取 100 条消息到本地缓存
        factory.setPrefetchCount(100);
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
        //x-message-ttl 参数的单位是毫秒。
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
        //x-message-ttl 参数的单位是毫秒。
        map.put("x-message-ttl", RETRY_INTERVAL);
        map.put("x-dead-letter-exchange", DIRECT_EXCHANGE_NAME);
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
        //  this(name, true, false);(String name, boolean durable, boolean autoDelete)
        //交换机默认持久化true
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE_NAME);
        return directExchange;
    }

    /**
     * 修改队列信息，要把之前的队列删除，重新建队列更改才会生效
     *
     *
     * Default for virtual host参数：
     *
     * Auto expire : 队列生存期，单位毫秒，队列多长时间没有被使用(访问)就会被删除。换句话说就是，当队列在指定的时间内没有被使用(访问)就会被删除。
     * Message TTL：消息生存期，单位毫秒。可以用作延迟队列，消息延迟消费等场景。
     * Overflow behaviour：设置队列溢出行为，队列中的消息溢出后如何处理。这决定了当达到队列的最大长度时消息会发生什么。有效值是drop-head、reject-publish或reject-publish-dlx(将溢出的新消息转发到指定的死信交换机（DLX）)。仲裁队列类型仅支持drop-head。
     * Single active consumer：表示队列是否是单一活动消费者，true时，注册的消费组内只有一个消费者消费消息，其他被忽略，false时消息循环分发给所有消费者(默认false)。
     * Dead letter exchange：死信队列交换机名称，过期或溢出被删除（因队列长度超长或因空间超出阈值）的消息可指定发送到该交换器中。
     * Dead letter routing key：死信消息路由键，当消息发送到死信交换器时会使用该路由键，如果不设置，则使用消息的原来的路由键值。
     * Max length：队列最大长度，可以理解为队列可以容纳的消息的最大条数。超过该最大值，则将从队列头部开始删除消息。
     * Max length bytes：队列消息内容占用对打空间，可以理解为队列可以容纳的消息的最大字节数，受限服务器内存大小，超过该阈值则从队列头部开始删除消息。
     * Leader locator：设置在节点集群上声明队列前导时定位的规则。有效值为client-local(默认值)和balanced。
     * @return
     */
    @Bean
    public Queue directQueue() {

//        durable=true（队列持久化）：仅保证队列的元数据在RabbitMQ重启后不丢失。
//
//        delivery_mode=2（消息持久化）：才决定消息内容是否写入磁盘。
        //设置死信队列的参数（交换机、路由key）
        // Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        HashMap<String, Object> args = new HashMap<>();

        //       RabbitMQ的默认行为是假设队列长度无限，
        // ========== 强制配置 ==========
        // 1. 最大消息数量（防止无限堆积），修改队列的属性要把之前的队列删除否则不生效
//        args.put("x-max-length", 2);

        // 2. 最大队列字节大小（防止大消息撑爆内存） //x-max-length 和 x-max-length-bytes 是同时生效的.队列长度限制 = min(数量限制，字节限制)
//        args.put("x-max-length-bytes", 1024 * 1024 * 500); // 500MB

        // 3. 设置消息TTL（自动清理旧消息）
//        args.put("x-message-ttl", 24 * 60 * 60 * 1000); // 24小时

        //

//        队列溢出行为	队列满时处理新消息	x-overflow	drop-head (默认), reject-publish	生产者速度过快，需要保护队列不无限增长。

        // 4. 溢出策略（推荐使用reject-publish），默认  静默drop-head策略。删除最早的，不会有任何通知
        args.put("x-overflow", "reject-publish");
        //----end------------


        //设置队列最大优先级[0,9]，发送消息时候指定优先级
        args.put("x-max-priority", 10);
        //  x-message-ttl 参数的单位是毫秒。
//        args.put("x-message-ttl", 30000);
        // 设置该Queue的死信的队列
        args.put("x-dead-letter-exchange", DIRECT_EXCHANGE_NAME);
        // 设置死信routingKey
        args.put("x-dead-letter-routing-key", DIRECT_ROUTING_KEY_DLX);
        //rabbitmq 默认发送给所有消费中的一个，尽管集群也只会发给一个服务中的一个消费者
        args.put("x-single-active-consumer", true);

//        设置 x-single-active-consumer=true 开启单活模式，不设置则默认是轮询模式
//        true	多个消费者订阅同一队列时，只有一个活跃消费者处理消息，其他作为备份；活跃者故障时自动切换	需要保证消息顺序消费 + 高可用性
//        false（默认）	多个消费者轮流消费消息（Round-robin 轮询），所有消费者同时工作	提升吞吐量，不要求严格顺序
//                         所有消费者同时活跃
//                         消息依次轮流分发（Round-robin 轮询）给各个消费者
//                        每个消息只被一个消费者处理（不像 Fanout 交换机那样广播）
//
//        实际应用建议
//        场景	            推荐模式	                           理由
//        高吞吐量，不关心顺序	默认轮询模式	                      多个消费者并行处理，充分利用资源
//        需要顺序消费	    SAC 模式	                          单一消费者保证顺序
//        需要顺序 + 高可用	SAC 模式	                          主备切换，防止单点故障
//        需要顺序 + 高吞吐	Super Streams + 分区	              分区内顺序，跨分区并行


        //sac:单活队列
//        map.put("x-single-active-consumer", true);
//        HashMap<String,Object> args = new HashMap<String,Object>();
//        args.put("x-single-active-consumer", true);
//        QueueBuilder.durable(DIRECT_QUEUE_NAME).withArguments(args).build();
        //this(name, true, false, false);  (String name, boolean durable, boolean exclusive, boolean autoDelete)
        //队列默认持久化：true
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
