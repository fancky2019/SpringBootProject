package com.example.demo.model.entity.wms;

import java.math.BigDecimal;

public class OrderDetail {
    private Integer id;

    private String guid;

    private Integer orderid;

    private Integer productid;

    private Integer count;

    private BigDecimal dealprice;

    private Short status;

    public OrderDetail(Integer id, String guid, Integer orderid, Integer productid, Integer count, BigDecimal dealprice, Short status) {
        this.id = id;
        this.guid = guid;
        this.orderid = orderid;
        this.productid = productid;
        this.count = count;
        this.dealprice = dealprice;
        this.status = status;
    }

    public OrderDetail() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getDealprice() {
        return dealprice;
    }

    public void setDealprice(BigDecimal dealprice) {
        this.dealprice = dealprice;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}