package com.example.demo.model.entity.valvulas;

import java.util.Date;

public class ValvulasProduct {
    private Integer id;

    private String guid;

    private String productname;

    private String productstyle;

    private String imagepath;

    private Date creadtetime;

    private Date modifytime;

    private Short status;

    private String description;

    private Date timestamp;

    public ValvulasProduct(Integer id, String guid, String productname, String productstyle, String imagepath, Date creadtetime, Date modifytime, Short status, String description, Date timestamp) {
        this.id = id;
        this.guid = guid;
        this.productname = productname;
        this.productstyle = productstyle;
        this.imagepath = imagepath;
        this.creadtetime = creadtetime;
        this.modifytime = modifytime;
        this.status = status;
        this.description = description;
        this.timestamp = timestamp;
    }

    public ValvulasProduct() {
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

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname == null ? null : productname.trim();
    }

    public String getProductstyle() {
        return productstyle;
    }

    public void setProductstyle(String productstyle) {
        this.productstyle = productstyle == null ? null : productstyle.trim();
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath == null ? null : imagepath.trim();
    }

    public Date getCreadtetime() {
        return creadtetime;
    }

    public void setCreadtetime(Date creadtetime) {
        this.creadtetime = creadtetime;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}