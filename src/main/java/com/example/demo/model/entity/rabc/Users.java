package com.example.demo.model.entity.rabc;

import java.util.Date;
/**
 * 1、用户 、 菜单、角色、用户角色、角色菜单
 *
 * 2、用户 组  用户组 角色 组角色 权限（菜单表） 角色权限：比1多了个用户组的封装
 *
 *
 *
 */
public class Users {
    private Integer id;

    private String account;

    private String password;

    private Short status;

    private Date createtime;

    private Date moditytime;
    /*
       注意：修改mapper文件的_byte为Byte
     */
    private Byte[] timestamp;

    public Users(Integer id, String account, String password, Short status, Date createtime, Date moditytime) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.status = status;
        this.createtime = createtime;
        this.moditytime = moditytime;
    }

    public Users(Integer id, String account, String password, Short status, Date createtime, Date moditytime, Byte[] timestamp) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.status = status;
        this.createtime = createtime;
        this.moditytime = moditytime;
        this.timestamp = timestamp;
    }

    public Users() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getModitytime() {
        return moditytime;
    }

    public void setModitytime(Date moditytime) {
        this.moditytime = moditytime;
    }

    public Byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Byte[] timestamp) {
        this.timestamp = timestamp;
    }
}