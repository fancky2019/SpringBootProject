package com.example.demo.model.entity.rabc;

import java.util.Date;

public class Roles {
    private Integer id;

    private String name;

    private Date createtime;

    private String remark;

    public Roles(Integer id, String name, Date createtime, String remark) {
        this.id = id;
        this.name = name;
        this.createtime = createtime;
        this.remark = remark;
    }

    public Roles() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}