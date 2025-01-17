package com.example.demo.model.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class Page implements Serializable {
    private Integer pageSize;
    private Integer pageIndex;
    private Integer maxId;
    private Integer offset;

    private List<String> exportFieldList;
    private LinkedHashMap<String,String> fieldMap;
    //mysql 无法limit 运算
//select * from demo_product         where 1=1                   limit 5000*(1-1) ,5000
    private Integer getOffset() {
        return (pageIndex - 1) * pageSize;
    }
}
