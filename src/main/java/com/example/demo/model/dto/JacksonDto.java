package com.example.demo.model.dto;

import com.example.demo.model.entity.demo.Person;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class JacksonDto {
    private Integer id;

    private String cityName;

    private List<Person> person;

    private String grade;

    private Date birthday;
    //    //前端传给后端不能解析值
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
