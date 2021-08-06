package com.example.demo.model.entity.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * demo_product
 * @author 
 */
@Data
public class DemoProduct implements Serializable {
    private Integer id;

    private String guid;

    private String productName;

    private String productStyle;

    private String imagePath;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private Short status;

    private String description;

    private LocalDateTime timestamp;

    private static final long serialVersionUID = 1L;
}