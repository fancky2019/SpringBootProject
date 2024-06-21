package com.example.demo.model.pojo;

import lombok.Data;

import java.util.List;

@Data
public class EsRequestPage extends Page{
    private List<String> sourceField;
    private List<Sort> sortList;
}
