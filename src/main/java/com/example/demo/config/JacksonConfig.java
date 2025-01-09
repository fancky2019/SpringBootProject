package com.example.demo.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper ObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 日期和时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));


//        SimpleModule module = new SimpleModule();
        // "" 不序列化成null
        javaTimeModule.addSerializer(String.class, new JsonSerializer<String>() {
            @Override
            public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//                if (value == null || value.isEmpty()) {
                if (value == null) {
                    gen.writeNull(); // 序列化为 null
                } else {
                    gen.writeString(value); // 正常序列化
                }
            }
        });
//        objectMapper.registerModule(module);


        objectMapper.registerModule(javaTimeModule);
//        //空处理成“”
//        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object paramT, JsonGenerator paramJsonGenerator,
//                                  SerializerProvider paramSerializerProvider) throws IOException {
//                //设置返回null转为 空字符串""
//                paramJsonGenerator.writeString("");
//            }
//        });

        /*
        //1.实体上

        @JsonInclude(Include.NON_NULL)

        //将该标记放在属性上，如果该属性为NULL则不参与序列化
        //如果放在类上边,那对这个类的全部属性起作用
        //Include.Include.ALWAYS 默认
        //Include.NON_DEFAULT 属性为默认值不序列化
        //Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
        //Include.NON_NULL 属性为NULL 不序列化

        //2.代码上
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);

        //通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
        //Include.Include.ALWAYS 默认
        //Include.NON_DEFAULT 属性为默认值不序列化
        //Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
        //Include.NON_NULL 属性为NULL 不序列化

         */

        //json 序列化null值。字符串“” 序列化成null, 默认序列化：  "serialNo": null,
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//       //忽略未知字段 ,不然字段不匹配会报错
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//
//
//
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
//       // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//       // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
////        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
////        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
////        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return objectMapper;

    }

}
