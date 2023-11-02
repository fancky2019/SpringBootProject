package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class KafkaConsumerClient {

    /*
    消息被广播到所有订阅的消费者组。
     kafka目前只提供单个分区内的消息顺序，而不会维护全局消息顺序
     要指定消费者组，不指定会被其他微服务重复消费
     指定消费者组，避免多组重复消费。
     同一消息会被不通消费组消费，造成重复消费。不同消费者组消费不同topic,
     一个消息只会被一个消费组内的一个消费者消费
     */
    @KafkaListener(topics = "test_topic",groupId = "test_group")
    public void processMessage(ConsumerRecord<?, ?> record) throws Exception {
        //  Integer m=Integer.parseInt("m");
        //spring 封装的自动commit()
        //出现异常添加redis里处理。
        System.out.printf("topic = %s, offset = %d, value = %s \n", record.topic(), record.offset(), record.value());
    }

    //批量消费
//    @KafkaListener(topics = "test_topic")
//    public void processMessage(List<ConsumerRecord<?, ?>> records) throws Exception {
//
//        records.forEach(record->
//        {
//            System.out.printf("topic = %s, offset = %d, value = %s \n", record.topic(), record.offset(), record.value());
//
//        });
//   }

    //没调试通
//    @KafkaListener(topics = "test_topic")
//    public void listen(ConsumerRecord<?, ?> record, Acknowledgment ack) throws Exception {
//        try {
//
//            int m = 0;
//            System.out.printf("topic = %s, offset = %d, value = %s \n", record.topic(), record.offset(), record.value());
//
//            //            ack.acknowledge();
//        } catch (Exception ex) {
//            //异常处理
//        }
//    }
}
