package com.example.demo.config;

import com.example.demo.listener.RouterMessageListener;
import com.example.demo.listener.redis.RedisStreamConfig;
import com.example.demo.model.entity.demo.MqMessage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * 添加此配置类解决框架redis序列化存储乱码问题
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 日期和时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);

        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        return template;

    }

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplateObj(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        return template;

    }

    // 专门的 StringRedisTemplate 用于 Stream 操作
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     *
     * redis 缓存配置
     * 配置cacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        //默认的redisCahce配置，此处我们没用，而是自己配置
        //RedisCacheConfiguration.defaultCacheConfig();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存的默认过期时间
                .entryTtl(Duration.ofSeconds(180))
                //null值设置过期时间暂未实现
                // 自定义前缀格式，redis 缓存默认::,设置：
                .computePrefixWith(cacheName -> cacheName + ":")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.jackson2JsonRedisSerializer()));
        // 不缓存空值
//                .disableCachingNullValues();
        //根据redis缓存配置和reid连接工厂生成redis缓存管理器
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
        return redisCacheManager;
    }


    /***
     * 配置jackson2JsonRedisSerializer
     * @return
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;

    }


    //region  redis stream. 消费者 在 listener 包下

    @Autowired
    private RedisStreamConfig redisStreamConfig;
    @Autowired
    private RouterMessageListener routerMessageListener;

//    // 用于在应用启动时初始化 Stream 和 Consumer Group
//    @EventListener(ApplicationReadyEvent.class)
//    public void initStreamAndConsumerGroup(RedisTemplate<String, Object> redisTemplate) {
//
//        //kafka的topic 或者rabbitmq 的queue
//        String streamKey = "my-stream";
//        String groupName = "my-group";
//
//        try {
//
//            StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(streamKey);
//
//            // XInfoGroups 提供了 stream() 方法，可以直接使用 Stream API
//            boolean groupExists = groups.stream()
//                    .anyMatch(g -> groupName.equals(g.groupName()));
//            if (!groupExists) {
//                // 组不存在，创建组（如果 Stream 不存在也会自动创建）
//                redisTemplate.opsForStream()
//                        .createGroup(streamKey, ReadOffset.from("0"), groupName);
//                log.info("Redis Stream消费者组创建成功: stream={}, group={}", streamKey, groupName);
//            } else {
//                log.info("Redis Stream消费者组已存在: stream={}, group={}", streamKey, groupName);
//            }
//        } catch (Exception e) {
//            // 如果 Stream 不存在，groups() 方法会抛出异常
//            // 直接创建 Stream 和 Group
//            try {
//                redisTemplate.opsForStream()
//                        .createGroup(streamKey, ReadOffset.from("0"), groupName);
//                log.info("Redis Stream和消费者组创建成功: stream={}, group={}", streamKey, groupName);
//            } catch (Exception ex) {
//                log.warn("创建 Redis Stream/Group 失败: {}", ex.getMessage());
//            }
//        }
//    }


    //    @EventListener(ApplicationReadyEvent.class)//Spring 容器启动完成之后执行
    public void initAllStreamsAndGroups(StringRedisTemplate redisTemplate) {
        Set<String> streamKeys = routerMessageListener.getHandlerMap().keySet();
//        List<String> streamKeys= redisStreamConfig.getStreamKeys();
        for (String streamKey : streamKeys) {
            String prefixStreamKey = streamKey;// buildStreamKey(streamKey);
            try {
                redisTemplate.opsForStream()
                        .createGroup(prefixStreamKey, ReadOffset.from("0"), redisStreamConfig.getGroupName());
                log.info("初始化成功: stream={}, group={}", prefixStreamKey, redisStreamConfig.getGroupName());
            } catch (RedisSystemException e) {
                if (e.getMessage().contains("BUSYGROUP")) {
                    log.debug("消费者组已存在: stream={}, group={}", prefixStreamKey, redisStreamConfig.getGroupName());
                } else {
                    log.warn("初始化失败: stream={}, error={}", prefixStreamKey, e.getMessage());
                }
            }
        }
    }


    private String buildStreamKey(String apiName) {
        return String.format("stream:%s", apiName);
    }


    //单个stream  消息体中包含apiName。之前event 是这么处理


//    @Bean
//    public StreamMessageListenerContainer<String, ObjectRecord<String, Object>> container(
//            RedisConnectionFactory factory,
//            RedisStreamListener listener) {
//
//        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, Object>> options =
//                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
//                        // 轮询超时时间
//                        .pollTimeout(Duration.ofSeconds(1))
//                        // 目标类型，用于反序列化
//                        .targetType(Object.class)
//                        .build();
//
//        StreamMessageListenerContainer<String, ObjectRecord<String, Object>> container =
//                StreamMessageListenerContainer.create(factory, options);
//        // 3. 注册订阅（指定消费者组、消费者名、Stream及监听器）
//        container.receive(
//                Consumer.from("my-group", "consumer-1"),
//                StreamOffset.create("my-stream", ReadOffset.lastConsumed()),
//                listener
//        );
//
//        container.start();
//        return container;
//    }

//    /**
//     * 发送 API 消息
//     * @param apiName Stream 的 Key（消息队列的名称）
//     * @param request 请求数据
//     */
//    public void sendApiMessage(String apiName, Object request) {
//
//
//        ObjectRecord<String, Object> record = StreamRecords.newRecord()
    // Stream 的 Key（消息队列的名称）
//                .in(apiName)
//                .ofObject(request);
//
//        RecordId recordId = redisTemplate.opsForStream().add(record);
//        log.info("消息发送成功: apiName={}, requestId={}, recordId={}",
//                apiName, message.getRequestId(), recordId);
//    }

    /**
     * 为每个 API 创建独立的监听容器
     */
    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, MqMessage>> multiStreamContainer(
            RedisConnectionFactory factory,
            StringRedisTemplate redisTemplate,
            @Qualifier("routerMessageListener") StreamListener<String, ObjectRecord<String, MqMessage>> listener
    ) {
        initAllStreamsAndGroups(redisTemplate);


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, MqMessage>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(1))
//                        .targetType(Object.class)     // Spring 不知道具体类型，返回 LinkedHashMap 或 byte[]
                        .targetType(MqMessage.class)
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, MqMessage>> container =
                StreamMessageListenerContainer.create(factory, options);
//        List<String> streamKeys=  redisStreamConfig.getStreamKeys();
        Set<String> streamKeys = routerMessageListener.getHandlerMap().keySet();
        // 为每个 API 注册订阅
        for (String streamKey : streamKeys) {
            String prefixStreamKey = streamKey;// buildStreamKey(streamKey);
            container.receive(
                    Consumer.from(redisStreamConfig.getGroupName(), redisStreamConfig.getConsumerName() + "-" + streamKey),
                    StreamOffset.create(prefixStreamKey, ReadOffset.lastConsumed()),
                    listener
            );
            log.info("注册监听: stream={}, consumer={}", prefixStreamKey, redisStreamConfig.getConsumerName() + "-" + streamKey);
        }

        container.start();
        return container;
    }
    //endregion


}
