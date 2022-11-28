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
public class DownloadData {

    //@ExcelIgnore 不导出列
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
}