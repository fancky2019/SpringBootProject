package com.example.demo.rabbitMQ.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.model.viewModel.Person;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectExchangeProducer {

    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot.Direct";
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";


    @Autowired
    private RabbitTemplate rabbitTemplate;

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

}
