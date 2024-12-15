package com.example.demo.model.entity.rabc;

/**
 * 1、用户 、 菜单、角色、用户角色、角色菜单
 *
 * 2、用户 组  用户组 角色 组角色 权限（菜单表） 角色权限：比1多了个用户组的封装
 *
 *
 *
 */
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