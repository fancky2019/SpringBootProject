package com.example.demo.listener.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Group	管理消费进度、负载均衡	Kafka 的 Consumer Group
 * Consumer	标识具体的消费者实例	组内的成员编号
 * 没有 Group：无法记录消费进度，重启后会重复消费或丢失消息
 * 没有 Consumer：无法区分不同的实例，负载均衡和故障恢复都无法实现
 *
 *
 *
 * 配置的时候一个消费者组 下面消费者名称
 */
@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "redis-stream")
public class RedisStreamConfig {
    //Spring 的 @Value 注解默认不支持直接注入 List<String>。当你使用 @Value("${redis-stream.api-names}") 时，Spring 会尝试将整个配置值作为字符串赋值给 List，导致类型转换错误。
    private List<String> streamKeys;
//  # 组名：按业务模块划分
    private String groupName="processor-group-1";  // 直接赋默认值;
//# 消费者名：包含实例标识，便于排查问题.Kafka、RocketMQ(clientId） 自动生成的
    private String consumerName="consumer-1";
    @PostConstruct
    public void init() {
        // 动态生成唯一的 consumerName
        if (consumerName == null || "consumer-1".equals(consumerName)) {
            String ip = getLocalIp();
            String hostname = getHostname();
            int pid = getPid();

            // 格式类似 RocketMQ: ip@hostname@pid
            this.consumerName = String.format("%s@%s@%d", ip, hostname, pid);

            log.info("动态生成 consumerName: {}", this.consumerName);
        }
    }

    private String getLocalIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (Exception e) {
            return "unknown-ip";
        }
    }

    private String getHostname() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (Exception e) {
            return "unknown-host";
        }
    }

    private int getPid() {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.contains("@")) {
            return Integer.parseInt(processName.split("@")[0]);
        }
        return ThreadLocalRandom.current().nextInt(10000, 99999);
    }

    /**
     *
     * Kafka、RocketMQ 会启动时候 SDK 会自动生成一个全局唯一的字符串来代表自己（clientId） 注册到服务端：
     *
     * 中间件	生成的客户端标识 (clientId) 格式	示例
     * Kafka	consumer-{group.id}-{uuid或hostname}-{timestamp}-{partition.assigned}	consumer-order-group-1-server-a-1234-0
     * RocketMQ	{ip}@{instanceName} 或 {groupName}@{pid}@{hostname}	192.168.1.100@order-consumer
     *
     *
     */
}
