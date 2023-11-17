package com.example.demo.model.request;

import com.example.demo.model.pojo.Page;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
public class DemoProductRequest extends Page {
    private Integer id;
    private String guid;
    private String productName;
    private String productStyle;
    private String imagePath;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
    private LocalDateTime modifyTime;
    private long status;
    private String description;
    private LocalDateTime timestamp;
}
