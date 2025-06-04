package com.example.demo.model.request;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.demo.model.pojo.EsRequestPage;
import com.example.demo.model.pojo.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class EntityBaseRequest extends EsRequestPage {
    private static final long serialVersionUID = 1L;
    public BigInteger id;
    public LocalDateTime createTime;
    public LocalDateTime modifyTime;
    public Integer version;
    public boolean deleted;
    public String traceId;
}
