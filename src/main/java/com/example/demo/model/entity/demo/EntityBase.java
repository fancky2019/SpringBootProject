package com.example.demo.model.entity.demo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
// 禁用链式调用，否则easyexcel读取时候无法生成实体对象的值
@Accessors(chain = false)
public class EntityBase {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "id")
//    @ColumnWidth(25)
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    //雪花id js number 精度丢失要转成string.前段js long 精度丢失
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger id;

    @ExcelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ExcelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;

    @ExcelProperty(value = "版本号")
    private Integer version;

    @ExcelProperty(value = "删除")
    private boolean deleted;

    @ExcelProperty(value = "traceId")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String traceId;
}
