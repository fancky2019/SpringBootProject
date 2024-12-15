package com.example.demo.model.entity.rabc;
/**
 * 1、用户 、 菜单、角色、用户角色、角色菜单
 *
 * 2、用户 组  用户组 角色 组角色 权限（菜单表） 角色权限：比1多了个用户组的封装
 *
 *
 *
 */
public class Menus {
    private Integer id;

    private Integer parentid;

    private String formname;

    private String tabheadertext;

    private Integer sortcode;

    private String remark;

    private Short status;

    public Menus(Integer id, Integer parentid, String formname, String tabheadertext, Integer sortcode, String remark, Short status) {
        this.id = id;
        this.parentid = parentid;
        this.formname = formname;
        this.tabheadertext = tabheadertext;
        this.sortcode = sortcode;
        this.remark = remark;
        this.status = status;
    }

    public Menus() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname == null ? null : formname.trim();
    }

    public String getTabheadertext() {
        return tabheadertext;
    }

    public void setTabheadertext(String tabheadertext) {
        this.tabheadertext = tabheadertext == null ? null : tabheadertext.trim();
    }

    public Integer getSortcode() {
        return sortcode;
    }

    public void setSortcode(Integer sortcode) {
        this.sortcode = sortcode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}