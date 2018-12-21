package com.example.demo.model.entity;

public class Authorities {
    private Integer id;

    private Integer menuid;

    private String name;

    private String remark;

    public Authorities(Integer id, Integer menuid, String name, String remark) {
        this.id = id;
        this.menuid = menuid;
        this.name = name;
        this.remark = remark;
    }

    public Authorities() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuid() {
        return menuid;
    }

    public void setMenuid(Integer menuid) {
        this.menuid = menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}