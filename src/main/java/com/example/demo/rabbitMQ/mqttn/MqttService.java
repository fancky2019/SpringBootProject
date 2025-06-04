package com.example.demo.rabbitMQ.mqttn;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MqttService {

    private static final Logger log = LoggerFactory.getLogger(MqttService.class);
    private final MessageChannel mqttOutboundChannel;

    @Autowired
    public MqttService(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    public String sendMessage(String topic, String payload) {
        MqttMessage message = new MqttMessage();
        message.setMsgId(UUID.randomUUID().toString());
        message.setTopic(topic);
        message.setPayload(payload);

        Message<String> mqttMessage = MessageBuilder.withPayload(payload)
                .setHeader("mqtt_topic", topic)
                .setHeader("mqtt_qos", 1)
                .setHeader("mqtt_retained", false)
                //接收端收不到msgId， MQTT 消息头丢失:将 msgId 嵌入消息体
                .setHeader("msgId", message.getMsgId())
                //消息未发送到接收单，MQTT 5.0 支持 User Properties，可原生携带自定义元数据。
                .setHeader(MqttHeaders.CORRELATION_DATA, message.getMsgId()) // 使用预定义头
                .build();

       boolean success= mqttOutboundChannel.send(mqttMessage);
       if(success)
       {
           log.info("send msgId - {} success",message.getMsgId());
       }
       else {
           log.info("send msgId - {} fail",message.getMsgId());
       }
        return message.getMsgId();
    }

    @Data
    private static class MqttMessage {
        private String msgId;
        private String topic;
        private String payload;
    }
}