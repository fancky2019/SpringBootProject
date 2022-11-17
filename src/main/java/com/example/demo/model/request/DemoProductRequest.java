package com.example.demo.model.request;

import com.example.demo.model.pojo.Page;
import lombok.Data;

@Data
public class DemoProductRequest extends Page {
    private String productName;
}
