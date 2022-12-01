package com.example.demo.rabbitMQ.producer;

import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.model.viewModel.Person;
import com.example.demo.rabbitMQ.RabbitMqMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class DirectExchangeProducer {

    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    //    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot.Direct";
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot";

    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";


    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private BatchingRabbitTemplate batchingRabbitTemplate;
//    @Autowired
//    private  AsyncRabbitTemplate asyncRabbitTemplate;
//    @Autowired
//    private AmqpTemplate amqpTemplate;


    public void producer() {
//        String msg = "MSG_DirectExchangeProducer";
        //参数：队列名称,消息内容（可以为可序列化的对象）
//        this.amqpTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
//        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
        Person person = new Person();
        person.setId(1);
        person.setName("rabbitmq");

//        msg = JSONObject.toJSONString(person, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);


        ObjectMapper objectMapper = new ObjectMapper();
        String json="";
        try {
             json = objectMapper.writeValueAsString(person);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        RabbitMqMessage mqMsg = new RabbitMqMessage(json);
        mqMsg.setMessageId(UUID.randomUUID().toString());
       /*
    一、mandatory 参数
        当mandatory 参数设为 true 时，交换器无法根据自身的类型和路由键找到一个符合条件 的队列，那么 RabbitMQ 会调用 Basic.Return 命令将消息返回给生产者 。当 mandatory 数设置为 false 时，出现上述情形，则消息直接被丢弃,那么生产者如何获取到没有被正确路由到合适队列的消息呢?这时候可以通过调用 channel addReturnListener 来添加 ReturnListener 监昕器实现。

        二、immediate 参数  rabbitMQ3.0删除改参数
        imrnediate 参数设为 true 时，如果交换器在将消息路由到队列时发现队列上并不存在 任何消费者，那么这条消息将不会存入队列中。当与路由键匹配的所有队列都没有消费者时 该消息会通过 Basic Return 返回至生产者。
        。
     */

    /*
     投递消息的时候指定了交换机名称：就指定了交换机的类型，路由key ：根据交换机和队列的绑定关系交换机就可以将消息投递到对应的队列
     */
        //没有指定交换机采用默认的交换机名称“”，直接发到指定的队列，最好指定交换机名称
//        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
//        rabbitTemplate.convertAndSend(DIRECT_EXCHANGE_NAME, DIRECT_ROUTING_KEY, mqMsg);
//        //json转换
//        String jsonStr1 =   JSON.toJSONString(person);
//        Person p=JSON.parseObject(jsonStr1,Person.class);
//        Integer m=0;

        try {
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE_NAME, DIRECT_ROUTING_KEY, mqMsg);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //region batch
    public static final String BATCH_DIRECT_EXCHANGE_NAME = "BatchSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String BATCH_DIRECT_ROUTING_KEY = "BatchRoutingKeySpringBoot";
    public static final String BATCH_DIRECT_QUEUE_NAME = "BatchQueueSpringBoot";

    //插入后的结果：多条拼接成一条，不是多条记录。
    //{"name":"publishInBatch0"}{"name":"publishInBatch1"}{"name":"publishInBatch2"}{"name":"publishInBatch3"}{"name":"publishInBatch4"}{"name":"publishInBatch5"}{"name":"publishInBatch6"}{"name":"publishInBatch7"}{"name":"publishInBatch8"}{"name":"publishInBatch9"}
    public void publishInBatch() {

//        //参数：队列名称,消息内容（可以为可序列化的对象）
////        this.amqpTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
////        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
//        Person person = new Person();
//        for (int i = 0; i < 10; i++) {
//
//            person.setName(MessageFormat.format("publishInBatch{0}", i));
//
//            String msg = JSONObject.toJSONString(person, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
//            System.out.println("send:"+msg);
//
//            //批量发送不支持CorrelationData（消息的ID。类似官网的PublishSeqNo）
////            CorrelationData correlationData=new CorrelationData() ;
////            correlationData.setId(UUID.randomUUID().toString());
//
//            MessageProperties messageProperties = new MessageProperties();
//            Message message = new Message(msg.getBytes(), messageProperties);
//            batchingRabbitTemplate.send(BATCH_DIRECT_EXCHANGE_NAME,BATCH_DIRECT_ROUTING_KEY,message);
//        }
//        //json转换
//        String jsonStr1 =   JSON.toJSONString(person);
//        Person p=JSON.parseObject(jsonStr1,Person.class);
//        Integer m=0;
    }
    //endregion


    //region  delayed_message

    //demo:https://github.com/rabbitmq/rabbitmq-delayed-message-exchange


    //region  rabbitmq_delayed_message_exchange
    public static final String DELAYED_MESSAGE_EXCHANGE = "DelayedMessageSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String DELAYED_MESSAGE_KEY = "DelayedMessageRoutingKeySpringBoot";
    public static final String DELAYED_MESSAGE_QUEUE = "DelayedMessageQueueSpringBoot";

    //endregion


    /*

  doc -->Community Plugins 下载  rabbitmq_delayed_message_exchange 放到 C:\Program Files\RabbitMQ Server\rabbitmq_server-3.10.5\plugins

  C:\Program Files\RabbitMQ Server\rabbitmq_server-3.10.5\sbin 目录执行命令 rabbitmq-plugins enable rabbitmq_delayed_message_exchange

    延迟队列：只是在交换器上暂存，等过期时间到了 才会发往队列。
    通过 x-delayed-message 声明的交换机（具体代码请看下面config下的配置类交换机定义参数），它的消息在发布之后不会立即进入队列，先将消息保存至 Mnesia（一个分布式数据库管理系统，适合于电信和其它需要持续运行和具备软实时特性的 Erlang 应用。目前资料介绍的不是很多）
     */
    public void produceDelayedMessage() {
        Person person = new Person();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        String dateStr = dateTimeFormatter.format(LocalDateTime.now());
        person.setName(dateStr);

        String msg = JSONObject.toJSONString(person, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);
    /*
                     投递消息的时候指定了交换机名称：就指定了交换机的类型，路由key ：根据交换机和队列的绑定关系交换机就可以将消息投递到对应的队列
                     */
//        rabbitTemplate.convertAndSend(DELAYED_MESSAGE_EXCHANGE, msg);
//        CorrelationData correlationData = new CorrelationData("12345678909"+new Date());
        rabbitTemplate.convertAndSend(DELAYED_MESSAGE_EXCHANGE, DELAYED_MESSAGE_KEY, msg, messagePostProcessor ->
        {
            //设置消息持久化
            messagePostProcessor.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            //message.getMessageProperties().setHeader("x-delay", "6000");
                    /*
                     头部设置了x-delay参数就自动和x-delayed-message类型交换机关联。args.put("x-delayed-type", "direct");
                     */
            //  this.headers.put("x-delay", delay);
            messagePostProcessor.getMessageProperties().setDelay(6000);
            return messagePostProcessor;
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    //endregion

}
