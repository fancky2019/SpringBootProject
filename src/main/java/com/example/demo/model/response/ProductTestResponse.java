package com.example.demo.model.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.example.demo.easyexcel.DropDownSetField;
import com.example.demo.easyexcel.EnumConverterUtil;
import com.example.demo.model.entity.demo.EntityBase;
import com.example.demo.model.vo.EnumAnnotation;
import com.example.demo.model.vo.ProductTestStatusDropDown;
import com.example.demo.model.vo.ProductTestStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProductTestResponse  extends EntityBase implements Serializable {
    @ExcelProperty(value = "guid")
    private String guid;
    @ExcelProperty({"${productName}"})
//    @ExcelProperty(value = "产品名称")
    private String productName;

    @ExcelProperty({"${productStyle}"})
//    @ExcelProperty(value = "产品型号")
    private String productStyle;

    @ExcelProperty(value = "图片路径")
    private String imagePath;



    @DropDownSetField(sourceClass = ProductTestStatusDropDown.class)

    @ExcelProperty(value = "状态", converter = EnumConverterUtil.class)
    //    @ExcelProperty(value = "状态",converter = ProductTestStatusEnumConverter.class)

    @EnumAnnotation(enumClass = ProductTestStatusEnum.class)
    private Integer status;

    @ExcelProperty(value = "描述")
    private String description;

    @ExcelProperty(value = "时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime timestamp;
}
