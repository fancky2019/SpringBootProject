package com.example.demo.model.dto;

import com.example.demo.model.pojo.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class EosStudentDto  extends Page {
    private Integer id;

    private Integer eosStudentID;

    private String studentName;

    private String grade;

    private String phone;

    private String schoolName;

    private String relativeStateStr;

    private Boolean relativeState;

    private String userInfoId;

    private String userInfoStudentIds;

    //前端传给后端不能解析值
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


}
