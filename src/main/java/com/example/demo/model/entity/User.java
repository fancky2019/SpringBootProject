package com.example.demo.model.entity;

import java.util.Date;

public class User {
    private Integer id;

    private String account;

    private String password;

    private Short status;

    private Date createtime;

    private Date moditytime;

    private byte[] timestamp;

    public User(Integer id, String account, String password, Short status, Date createtime, Date moditytime) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.status = status;
        this.createtime = createtime;
        this.moditytime = moditytime;
    }

    public User(Integer id, String account, String password, Short status, Date createtime, Date moditytime, byte[] timestamp) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.status = status;
        this.createtime = createtime;
        this.moditytime = moditytime;
        this.timestamp = timestamp;
    }

    public User() {
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

    public byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(byte[] timestamp) {
        this.timestamp = timestamp;
    }
}