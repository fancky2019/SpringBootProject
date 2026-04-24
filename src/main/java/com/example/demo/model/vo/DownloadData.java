package com.example.demo.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.string.StringImageConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
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

    /**
     * 建议直接导出url ,在excel 中点击下载
     */
    @ExcelProperty("产品图片")
    private URL urlImage;

//建议直接导出url
//    // 方式1：用 File 类型
//    @ExcelProperty("产品图片_File")
//    private File fileImage;
//
//    // 方式2：用 InputStream 类型
//    @ExcelProperty("产品图片_InputStream")
//    private InputStream inputStreamImage;
//
////    // 方式3：用 URL 类型（推荐，可直接写网络地址）
////    @ExcelProperty("产品图片_URL")
////    private URL urlImage;
//
//    // 方式4：用 byte[] 类型
//    @ExcelProperty("产品图片_ByteArray")
//    private byte[] byteArrayImage;
//
//    // 方式5：String 类型必须指定转换器
//    @ExcelProperty(value = "产品图片_String", converter = StringImageConverter.class)
//    private String stringImagePath;
}