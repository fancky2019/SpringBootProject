package com.example.demo.model.entity.rabc;
/**
 * 1、用户 、 菜单、角色、用户角色、角色菜单
 *
 * 2、用户 组  用户组 角色 组角色 权限（菜单表） 角色权限：比1多了个用户组的封装
 *
 *
 *
 */
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