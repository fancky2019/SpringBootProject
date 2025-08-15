package com.example.demo.model.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {
    private Long count;
    private List<T> data;
    private Boolean  hasMore;
}
