package com.example.demo.model.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {
    private Integer count;
    private List<T> rows;
}
