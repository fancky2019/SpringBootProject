package com.example.demo.model.request;

import com.example.demo.model.pojo.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

/**
 *  @Valid
 * 依赖
 * spring-boot-starter-validation
 *  @Valid 或者 @Validated @RequestBody OutByAssignedInfoBo requestList
 *
 *  @Validated 是 @Valid  的增强
 */
@Data
public class TestRequest {
    @NotBlank(message = "姓名不能为空")
    private String name;
    private String email;
    private String password;
    @NotNull(message = "年龄不能为空")
    private Integer age;
    private List<String> jobList;
    private HashMap<String,String> hashMap;
//    @NotNull(message ="共保生效时间不能为空")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private LocalDateTime address;

//    @Valid
//    @NotEmpty(message = "学生不能为空")
//    private List<Student> studentList;
}
