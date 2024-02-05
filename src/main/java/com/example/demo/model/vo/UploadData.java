package com.example.demo.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class UploadData {
    @ExcelProperty(value = "列1", index = 0)
    private String string;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
   @ExcelProperty(value = "日期", index = 1)
    private Date date;
    @ExcelProperty(value = "列3", index = 2)
    private Double doubleData;
}