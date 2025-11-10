package com.example.demo.rabbitMQ.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
public class MqttConsumerCallBack implements MqttCallback {

    /**
     * 客户端断开连接的回调
     */
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("与服务器断开连接，可重连");
    }

    /**
     * 消息到达的回调
     *
     * 文件 etc/emqx.conf 中retry_interval  duration 默认 30s 等待一个超时间隔，如果没收到应答则重传消息。
     * 默认最大数据大小：1 MB最大可配置数据大小：256 MB
     *  QOS  1:  AtMostOnce,
     *       2:  AtLeastOnce,
     *       3:  ExactlyOnce
     * QoS 0，最多交付一次。
     * QoS 1，至少交付一次。
     * QoS 2，只交付一次。
     * 其中，使用 QoS 0 可能丢失消息，使用 QoS 1 可以保证收到消息，但消息可能重复，使用 QoS 2 可以保证消息既不丢失也不重复
     *
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Thread.sleep(30*1000);
        log.info(String.format("接收消息主题 : %s",topic));
        log.info(String.format("接收消息Qos : %d",message.getQos()));
        String msg=new String(message.getPayload());
        log.info(String.format("接收消息内容 : %s",msg));
        log.info(String.format("接收消息retained : %b",message.isRetained()));
    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            iMqttDeliveryToken.getMessage();
        } catch (MqttException e) {
            log.error("",e);
        }
        log.info("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }
}
