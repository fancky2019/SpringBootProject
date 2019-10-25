package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class KafkaConsumerClient {

    @KafkaListener(topics = "test_topic")
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
