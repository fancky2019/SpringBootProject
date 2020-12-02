package com.example.demo.rabbitMQ.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.model.viewModel.Person;
import org.springframework.amqp.core.AsyncAmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.BatchingRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.UUID;

@Component
public class DirectExchangeProducer {

    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot.Direct";
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
        String msg = "MSG_DirectExchangeProducer";
        //参数：队列名称,消息内容（可以为可序列化的对象）
//        this.amqpTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
//        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
        Person person = new Person();
        person.setName("rabbitmq");

        msg = JSONObject.toJSONString(person, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse);

        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);

//        //json转换
//        String jsonStr1 =   JSON.toJSONString(person);
//        Person p=JSON.parseObject(jsonStr1,Person.class);
//        Integer m=0;
    }

    //region batch
    public static final String BATCH_DIRECT_EXCHANGE_NAME = "BatchSpringBoot";
    // 路由键支持模糊匹配，符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词
    public static final String BATCH_DIRECT_ROUTING_KEY = "BatchRoutingKeySpringBoot";
    public static final String BATCH_DIRECT_QUEUE_NAME = "BatchQueueSpringBoot";
//    //插入后的结果：多条拼接成一条，不是多条记录。
//    //{"name":"publishInBatch0"}{"name":"publishInBatch1"}{"name":"publishInBatch2"}{"name":"publishInBatch3"}{"name":"publishInBatch4"}{"name":"publishInBatch5"}{"name":"publishInBatch6"}{"name":"publishInBatch7"}{"name":"publishInBatch8"}{"name":"publishInBatch9"}
//    public void publishInBatch() {
//
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
//    }
    //endregion


}
