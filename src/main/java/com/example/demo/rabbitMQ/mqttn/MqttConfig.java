package com.example.demo.rabbitMQ.mqttn;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.mqtt")
@ConditionalOnProperty(name = "spring.mqtt.enabled", havingValue = "false")
@Data
public class MqttConfig {
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
    private List<String> subscribe;
    private String publish;
    private int qos;
    private int completionTimeout;
    private boolean cleanSession;
    private int keepAliveInterval;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(cleanSession);
        options.setConnectionTimeout(completionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public DefaultPahoMessageConverter pahoMessageConverter() {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
//        converter.setPayloadAsBytes(false);
        converter.setPayloadAsBytes(true);
        return converter;
    }
}