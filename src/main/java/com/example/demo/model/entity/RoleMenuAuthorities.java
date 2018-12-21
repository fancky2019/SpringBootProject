package com.example.demo.model.entity;

public class RoleMenuAuthorities {
    private Integer id;

    private Integer authorityid;

    private Integer roleid;

    private Integer menuid;

    public RoleMenuAuthorities(Integer id, Integer authorityid, Integer roleid, Integer menuid) {
        this.id = id;
        this.authorityid = authorityid;
        this.roleid = roleid;
        this.menuid = menuid;
    }

    public RoleMenuAuthorities() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthorityid() {
        return authorityid;
    }

    public void setAuthorityid(Integer authorityid) {
        this.authorityid = authorityid;
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