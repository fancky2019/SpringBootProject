package com.example.demo.model.viewModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class ProductVM extends Page implements Serializable {
    private Integer id;
    private String guid;
    private Integer stockid;
    private String stockName;
    private Integer barcodeid;
    private String barcodeName;
    private Integer skuid;
    private String skuName;
    private String productname;
    private String productstyle;
    private BigDecimal price;
    private Date createtime;
    private String createtimeStr;
    private Short status;
    private Integer count;
    private Date modifytime;
    private String modifytimeStr;
    private Byte[] timestamp;

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
        this.guid = guid;
    }

    public Integer getStockid() {
        return stockid;
    }

    public void setStockid(Integer stockid) {
        this.stockid = stockid;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Integer getBarcodeid() {
        return barcodeid;
    }

    public void setBarcodeid(Integer barcodeid) {
        this.barcodeid = barcodeid;
    }

    public String getBarcodeName() {
        return barcodeName;
    }

    public void setBarcodeName(String barcodeName) {
        this.barcodeName = barcodeName;
    }

    public Integer getSkuid() {
        return skuid;
    }

    public void setSkuid(Integer skuid) {
        this.skuid = skuid;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductstyle() {
        return productstyle;
    }

    public void setProductstyle(String productstyle) {
        this.productstyle = productstyle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCreatetimeStr() {
        return createtimeStr;
    }

    public void setCreatetimeStr(String createtimeStr) {
        this.createtimeStr = createtimeStr;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public String getModifytimeStr() {
        return modifytimeStr;
    }

    public void setModifytimeStr(String modifytimeStr) {
        this.modifytimeStr = modifytimeStr;
    }

    public Byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Byte[] timestamp) {
        this.timestamp = timestamp;
    }

}
