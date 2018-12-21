package com.example.demo.model.entity;

import java.util.Date;

public class Order {
    private Integer id;

    private String guid;

    private String ordernumber;

    private Date createtime;

    private Integer ordertype;

    private Short status;

    public Order(Integer id, String guid, String ordernumber, Date createtime, Integer ordertype, Short status) {
        this.id = id;
        this.guid = guid;
        this.ordernumber = ordernumber;
        this.createtime = createtime;
        this.ordertype = ordertype;
        this.status = status;
    }

    public Order() {
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

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber == null ? null : ordernumber.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}