package com.example.demo.rabbitMQ.consumer;

import com.example.demo.model.pojo.Student;
import com.example.demo.rabbitMQ.BaseRabbitMqHandler;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMqMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BatchMessageListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
//public class BatchQueueListener implements BatchMessageListener {
//public class BatchQueueListener extends BaseRabbitMqHandler<RabbitMqMessage> {

public class BatchQueueListener extends BaseRabbitMqHandler {
    @Autowired
    private ObjectMapper objectMapper;

//    //批量接收处理
//    @RabbitListener(queues = RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME, containerFactory = "batchQueueRabbitListenerContainerFactory")
////    @Override
//    public void onMessageBatch(List<RabbitMqMessage> rabbitMqMessages,
//                               Channel channel,
//                               Message messages) {
//
//
//        for (int i = 0; i < rabbitMqMessages.size(); i++) {
//            RabbitMqMessage rabbitMqMessage = rabbitMqMessages.get(i);
//          //  Message message = messages.get(i);
////            long deliveryTag = message.getMessageProperties().getDeliveryTag();
////            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
////            String exchange = message.getMessageProperties().getReceivedExchange();
////            String queueName = message.getMessageProperties().getConsumerQueue();
//
////            super.onMessage(rabbitMqMessage, message, channel, (msg, ch) -> {
////                //业务处理
////                String msgContent = msg.getContent();
////                int m = Integer.parseInt("d");
////                log.info("MQ接收到消息jsonStr : " + msgContent);
////
////            });
//        }
////        if (messages.size() > 0) {
////            log.info("第一条数据是: {}", new String(messages.get(0).getBody()));
////        }
//    }

    //批量接收处理
//    @RabbitListener(queues = RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME, containerFactory = "batchQueueRabbitListenerContainerFactory")
//    @Override
    public void onMessageBatch(List<Message> messages, Channel channel)  {


        for (Message message : messages) {
            try {
                String json = new String(message.getBody());
                RabbitMqMessage rabbitMqMessage = objectMapper.readValue(json, RabbitMqMessage.class);


                long deliveryTag = message.getMessageProperties().getDeliveryTag();
                String routingKey = message.getMessageProperties().getReceivedRoutingKey();
                String exchange = message.getMessageProperties().getReceivedExchange();
                String queueName = message.getMessageProperties().getConsumerQueue();

                 super.onMessage(RabbitMqMessage.class, message, channel, (msg) -> {
                    //业务处理
                    String msgContent = msg.getContent();
                    int m = Integer.parseInt("d");
                    log.info("MQ接收到消息jsonStr : " + msgContent);

                });
            } catch (Exception e) {
                String msg = e.getMessage();
            }
        }
//        if (messages.size() > 0) {
//            log.info("第一条数据是: {}", new String(messages.get(0).getBody()));
//        }
    }


}
