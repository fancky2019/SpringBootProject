package com.example.demo.model.viewModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/*
springboot 2.4 以后hibernate-validator不支持
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>7.0.4.Final</version>
        </dependency>

添加依赖
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>



controller 类 的方法参数哦前加上  @Validated 注解



@NotEmpty 用在集合上面(不能注释枚举):Collection、Map、数组，是不能为null或者长度为0
@NotBlank用在String上面：尾部空格会被忽略
@NotNull用在所有类型上面



JSR提供的校验注解：
@Null   被注释的元素必须为 null
@NotNull    被注释的元素必须不为 null
@AssertTrue     被注释的元素必须为 true
@AssertFalse    被注释的元素必须为 false
@Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Size(max=, min=)   被注释的元素的大小必须在指定的范围内
@Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内
@Past   被注释的元素必须是一个过去的日期
@Future     被注释的元素必须是一个将来的日期
@Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式

    *
     * Bean Validation 中内置的 constraint
     * @Null   被注释的元素必须为 null
     * @NotNull    被注释的元素必须不为 null
     * @AssertTrue     被注释的元素必须为 true
     * @AssertFalse    被注释的元素必须为 false
     * @Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值
     * @Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值
     * @DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
     * @DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
     * @Size(max=, min=)   被注释的元素的大小必须在指定的范围内
     * @Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内
     * @Past   被注释的元素必须是一个过去的日期
     * @Future     被注释的元素必须是一个将来的日期
     * @Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式
     * Hibernate Validator 附加的 constraint
     * @NotBlank(message =)   验证字符串非null，且长度必须大于0
     * @Email  被注释的元素必须是电子邮箱地址
     * @Length(min=,max=)  被注释的字符串的大小必须在指定的范围内
     * @NotEmpty   被注释的字符串的必须非空
     * @Range(min=,max=,message=)  被注释的元素必须在合适的范围内
     *


Hibernate Validator提供的校验注解：
@NotBlank(message =)   验证字符串非null也不为“”，且长度必须大于0
@Email  被注释的元素必须是电子邮箱地址
@Length(min=,max=)  被注释的字符串的大小必须在指定的范围内
@NotEmpty   被注释的字符串的必须不为null和“”
@Range(min=,max=,message=)  被注释的元素必须在合适的范围内
 */
@Getter
@Setter
public class ValidatorVo {
    @NotBlank(message ="姓名不能为空")
    private String name;
    @Min(value = 1,message = "年龄必须是大于1的数字")
    private  Integer age;
    @Email(message = "邮箱不能为空")
    private  String email;
    @NotEmpty(message = "子女不能为空")
    private List<String> children;
    private String address;

//    @ApiModelProperty(value = "共享申请明细")
//    @Valid //嵌套对象要加不然不生效
//    @NotEmpty(message = "共享申请明细不能为空")
//    private List<ShareListTmpRequest> shareListTmpList;
}

