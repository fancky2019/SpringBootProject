package com.example.demo.rabbitMQ.mqttn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.acks.SimpleAcknowledgment;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * MQTT 的 QoS ACK 是 通信层面的成功标志，不是业务层面的；
 * 原生 Paho 没有暴露 ack 控制的 API，也没有设计延迟 ACK 或手动 ACK 的机制。
 *
 * Spring Integration 的 MqttPahoMessageDrivenChannelAdapter 封装了 Paho，但自己做了消息投递的“延迟确认”处理机制：
 * 拦截 messageArrived()；
 * 将消息封装成 Spring 的 Message 对象；
 * 附加一个 MqttAckCallback；
 * 直到你调用 ack() 才真正从内部确认消息（或标记为失败重试）。
 * 换句话说，Spring Integration 封装了一层“手动 ACK 管理器”。
 *
 *
 * 消息手动Ack  没有处理成功
 *
 *
 *
 * Clean Session = true（默认）
 * 每次客户端连接时都创建一个全新的会话
 * 断开连接后，Broker 会立即丢弃所有未确认的消息和会话状态
 * 客户端收不到断开期间发送给它的消息
 * 不支持消息重发
 *
 * Clean Session = false
 * 使用持久化会话
 * Broker 会保存客户端的订阅信息和未确认的消息
 * 客户端重连后可以继续接收未确认的消息
 * 支持消息重发机制
 */
@Configuration
@Slf4j
public class MqttIntegrationConfig {

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    private MqttPahoClientFactory mqttClientFactory;

    @Autowired
    private DefaultPahoMessageConverter pahoMessageConverter;

    // 入站通道
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel(); // 默认单线程
        // 或者使用 ExecutorChannel 实现多线程
        // return new ExecutorChannel(taskExecutor());
    }

    // 出站通道
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // 入站适配器(多主题订阅)     接收到消息
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttConfig.getClientId() + "-inbound", mqttClientFactory, mqttConfig.getSubscribe().toArray(new String[0]));
        adapter.setConverter(pahoMessageConverter);
        adapter.setQos(mqttConfig.getQos());
        adapter.setManualAcks(true);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // 出站处理器  发送消息
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(mqttConfig.getClientId() + "-outbound", mqttClientFactory);
        handler.setDefaultTopic(mqttConfig.getPublish());
        handler.setDefaultQos(mqttConfig.getQos());
        handler.setConverter(pahoMessageConverter);
        return handler;
    }

//    // 入站消息处理流
//    @Bean
//    public IntegrationFlow mqttInFlow() {
//        return IntegrationFlows.from(mqttInputChannel())
//                .handle(this::processMessage)
//                .get();
//    }

//    @Bean
//    public DefaultPahoMessageConverter pahoMessageConverter() {
//        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
//        converter.setPayloadAsBytes(true);
//        return converter;
//    }

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInputChannel()).handle((payload, headers) -> {
            String content = new String((byte[]) payload);
            log.info("payload={}, headers={}", payload, headers);
            log.info("MqttReceiveMsg: content= {}", content);
            // clean-session: false ,重启会重新投递一次
//                    int m = Integer.parseInt("m");
            try {
                //默认单线程处理，可配置mqttInputChannel 实现多线程，但是顺序性无法保证
//                        Thread.sleep(10 * 1000);
                // 模拟业务处理
                int m = Integer.parseInt("m");
//                    MessageHeaders  MqttPahoMessageDrivenChannelAdapter.
                //Spring Integration 6.x（支持 Spring Boot 3.x）才新增该类Acknowledgment。
                SimpleAcknowledgment ack = headers.get("acknowledgmentCallback", SimpleAcknowledgment.class);
                if (ack != null) {
                    ack.acknowledge();
                    log.info("手动ACK成功");
                } else {
                    log.info("手动ACK失败");
                }

            } catch (Exception e) {
                log.error("处理失败，不ACK，等待重发", e);
                // 不调用 ack.acknowledge()
                //clean-session: false ,重启会重新投递一次
                throw e;
            }
            return null;
        }).get();
    }

    /**
     *
     * 处理接收的消息
     * @param message
     */
    private void processMessage(org.springframework.messaging.Message<?> message) {
//        Acknowledgment acknowledgment = message.getHeaders().get("mqtt_acknowledgment", Acknowledgment.class);
//        @Header("mqtt_acknowledgment") Acknowledgment acknowledgment
//        @Header(MqttHeaders.ACKNOWLEDGMENT) Acknowledgment acknowledgment
        String msgId = message.getHeaders().getOrDefault("msgId", UUID.randomUUID()).toString();
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        //没有接收到
        String id = (String) message.getHeaders().get(MqttHeaders.CORRELATION_DATA);
        String payload = message.getPayload().toString();

        log.info("Received message [ID: " + msgId + "]");
        log.info("Topic: " + topic);
        log.info("Payload: " + payload);
        log.info("Headers: " + message.getHeaders());

        int m = Integer.parseInt("m");
        switch (topic) {
            case "test":
                log.info("test");
                break;
            default:
                log.info("default");
                break;
        }


        // 2. 业务成功后才手动确认
//        acknowledgment.acknowledge();
    }
}
