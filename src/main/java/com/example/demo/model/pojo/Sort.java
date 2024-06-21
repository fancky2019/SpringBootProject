package com.example.demo.model.pojo;

import lombok.Data;

/**
 * @author lirui
 */
@Data
public class Sort {
    private  String sortField;
    /**
     * asc desc
     */
    private  String sortOrder;
}
