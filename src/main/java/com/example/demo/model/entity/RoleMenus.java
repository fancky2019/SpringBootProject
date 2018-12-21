package com.example.demo.model.entity;

public class RoleMenus {
    private Integer id;

    private Integer roleid;

    private Integer menuid;

    public RoleMenus(Integer id, Integer roleid, Integer menuid) {
        this.id = id;
        this.roleid = roleid;
        this.menuid = menuid;
    }

    public RoleMenus() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public Integer getMenuid() {
        return menuid;
    }

    public void setMenuid(Integer menuid) {
        this.menuid = menuid;
    }
}