package com.example.demo.model.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EasyPoiPojo {
    private Integer id;
    @Excel(name = "姓名", orderNum = "1")
    private String name;
    @Excel(name = "年龄", orderNum = "1")
    private Integer age;
    @Excel(name = "工作", orderNum = "1")
    private String job;
    @Excel(name = "地址", orderNum = "1")
    private String address;

}
