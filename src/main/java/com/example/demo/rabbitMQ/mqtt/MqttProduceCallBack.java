package com.example.demo.rabbitMQ.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;

public class MqttProduceCallBack implements MqttCallback {

    @Value("${spring.mqtt.client.id}")
    private String clientId;
    /**
     * 与服务器断开的回调
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println(clientId+"与服务器断开连接");
    }

    /**
     * 消息到达的回调
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    /**
     * 消息发布成功的回调
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        IMqttAsyncClient client = token.getClient();
        try {
            token.getMessage();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        System.out.println(client.getClientId()+"发布消息成功！");
    }
}