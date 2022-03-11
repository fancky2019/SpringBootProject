package com.example.demo.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
数据库字段是下划线   数据库用logstash同步到ES上  ES字段也是下划线
java 是小驼峰命名。es 字段也用小驼峰，那么logstash同步mysql数据到es,sql查询语句使用小驼峰别名。
如：select student_name as studentName from student;

或者使用@Field 注解
 */
@Document(indexName = "demo_product")
@Data
public class DemoProduct implements Serializable {
    //    @Id 如果和es的id 名称不一样映射es的id.
    private Integer id;

    @Field(name = "product_style")
    private String productStyle;
    /*
    java 实体和es字段名映射
     */
    @Field(name = "product_name",type = FieldType.Text)

//    @Field(analyzer = "ik_max_word",type = FieldType.Text)//中文分词设置
    private String productName;
//    @Field(name = "create_time")
    //es 到java实体时间的转换格式
    @Field(name = "create_time",index = true, store = true, type = FieldType.Date,format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


    @Field(name = "modify_time",index = true, store = true, type = FieldType.Date,format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime modifyTime;

    private String description;
    private String price;
    private String count;
    @Field(name = "produce_address")
    private String produceAddress;
}
