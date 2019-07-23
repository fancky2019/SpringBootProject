package com.example.demo.model.viewModel.rabc;

import com.example.demo.model.viewModel.Page;

import java.util.Date;

public class RolesVM extends Page {
    private Integer id;

    private String name;

    private Date createtime;

    private String remark;


    public RolesVM() {
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
