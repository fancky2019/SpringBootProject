package com.example.demo.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.text.MessageFormat;


//@Component
public class KafkaProducerClient {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void producer(String msg) {
        //发送消息指定key,消息保存在同一个分区。
        String partitionKey="table_name";
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("test_topic",partitionKey, msg);
        future.addCallback(success ->
                {
                    ProducerRecord<String, String> producerRecord = success.getProducerRecord();
                    String topic = producerRecord.topic();
                    String key = producerRecord.key();
                    String sendMsg = producerRecord.value();
                    System.out.println(MessageFormat.format("发送{0}成功!", producerRecord.value()));
                },
                failure ->
                {

                    System.out.println(MessageFormat.format("发送{0}失败!", msg));
                });
    }

}
