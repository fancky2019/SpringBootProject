package com.example.demo.model.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Page implements Serializable {
    private Integer pageSize;
    private Integer pageIndex;
}
