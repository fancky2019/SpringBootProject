package com.example.demo.rabbitMQ;

import com.example.demo.rabbitMQ.consumer.DirectExchangeConsumer;
import com.example.demo.rabbitMQ.producer.DirectExchangeProducer;
import com.example.demo.rabbitMQ.producer.FanoutExchangeProducer;
import com.example.demo.rabbitMQ.producer.TopicExchangeProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQTest {
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

        directExchangeProducer.producer();
        fanoutExchangeProducer.producer();
        topicExchangeProducer.producer();
    }

}
