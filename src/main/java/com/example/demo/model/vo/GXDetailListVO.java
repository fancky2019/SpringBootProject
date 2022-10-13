//package com.example.demo.model.vo;
//
//import com.alibaba.excel.annotation.ExcelIgnore;
//import com.alibaba.excel.annotation.ExcelProperty;
//import com.alibaba.excel.annotation.write.style.ColumnWidth;
//import com.alibaba.excel.annotation.write.style.HeadFontStyle;
//import com.alibaba.excel.annotation.write.style.HeadStyle;
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import org.apache.poi.ss.usermodel.IndexedColors;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Data
//@ApiModel(description = "共享业务清单明细导入请求对象")
//public class GXDetailListVO implements Serializable {
//    @ExcelIgnore
//    private static final long serialVersionUID = 1L;
//    @ExcelIgnore
//    private static final long serialVersionUID1 = 1L;
//    /**
//     * 保单号
//     */
//    @ApiModelProperty(value = "保单号",required=false)
//    @ExcelProperty(value = "*保单号")
//    @ColumnWidth(25)
//    @HeadStyle(fillBackgroundColor = 10)
//    private String policyNo;
//
//    @ExcelIgnore
//    private static final long serialVersionUID2 = 1L;
//
//    /**
//     * 分公司code
//     */
//    @ApiModelProperty(value = "分公司",required=false)
//    @ExcelProperty(value = "*分公司")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = BranchCodeDropDownSetImpl.class)
//    private String branchCode;
//
///*    *//**
//     * 保单类型
//     *//*
//    @ApiModelProperty(value = "保单类型",required=false)
//    @ExcelProperty(value = "保单类型")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = PolicyTypeDropDownSetImpl.class)
//    private String policyType;*/
//
//    /**
//     * 数据来源
//     */
//    @ApiModelProperty(value = "数据来源",required=false)
//    @ExcelProperty(value = "*数据来源")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = DataSourceDropDownSetImpl.class)
//    private String dataSource;
//
//    /**
//     * 数据状态
//     */
//    @ApiModelProperty(value = "数据状态",required=false)
//    @ExcelProperty(value = "数据状态")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = DataStatusDropDownSetImpl.class)
//    private String dataStatus;
//
//    /**
//     * 审核状态
//     */
//    @ApiModelProperty(value = "审核状态",required=false)
//    @ExcelProperty(value = "审核状态")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = AuditStatusDropDownSetImpl.class)
//    private String auditStatus;
//
//    /**
//     * 处理状态
//     */
//    @ApiModelProperty(value = "处理状态",required=false)
//    @ExcelProperty(value = "处理状态")
//    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = HandleStatusDropDownSetImpl.class)
//    private String handleStatus;
//
//    /**
//     * 保单生效时间
//     */
//    @ApiModelProperty(value = "保单生效时间",required=false)
//    @ExcelProperty(value = "保单生效时间")
//    @ColumnWidth(25)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date effectTime;
//
//    /**
//     * 投保人
//     */
//    @ApiModelProperty(value = "投保人",required=false)
//    @ExcelProperty(value = "投保人")
//    @ColumnWidth(25)
//    private String policyHolder;
//
//    /**
//     * 被保人
//     */
//    @ApiModelProperty(value = "被保人",required=false)
//    @ExcelProperty(value = "被保人")
//    @ColumnWidth(25)
//    private String insured;
//
//    /**
//     * 统保代码
//     */
//    @ApiModelProperty(value = "统保代码",required=false)
//    @ExcelProperty(value = "统保代码")
//    @ColumnWidth(25)
//    private String tbCode;
//
//    /**
//     * 签单保费
//     */
//    @ApiModelProperty(value = "签单保费",required=false)
//    @ExcelProperty(value = "签单保费")
//    @ColumnWidth(25)
//    private BigDecimal premium;
//
//    /**
//     * 保成利润率
//     */
//    @ApiModelProperty(value = "保成利润率",required=false)
//    @ExcelProperty(value = "保成利润率")
//    @ColumnWidth(25)
//    private BigDecimal profitScale;
//
//    /**
//     * 起保时间
//     */
//    @ApiModelProperty(value = "起保时间",required=false)
//    @ExcelProperty(value = "起保时间")
//    @ColumnWidth(25)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date startTime;
//
//    /**
//     * 终保时间
//     */
//    @ApiModelProperty(value = "终保时间",required=false)
//    @ExcelProperty(value = "终保时间")
//    @ColumnWidth(25)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date endTime;
//
//    /**
//     * 分公司
//     */
//    @ExcelIgnore
//    private String branchName;
//
//    /**
//     * 共保协议号
//     */
//    @ApiModelProperty(value = "共保协议号",required=false)
//    @ExcelProperty(value = "共保协议号")
//    @ColumnWidth(25)
//    private String gbAgrtCode;
//
//    /**
//     * 机构A分摊保费
//     */
//    @ApiModelProperty(value = "机构A分摊保费",required=false)
//    @ExcelProperty(value = "机构A分摊保费")
//    @ColumnWidth(25)
//    private BigDecimal ashareAmount;
//
//    /**
//     * 机构B分摊保费
//     */
//    @ApiModelProperty(value = "机构B分摊保费",required=false)
//    @ExcelProperty(value = "机构B分摊保费")
//    @ColumnWidth(25)
//    private BigDecimal bshareAmount;
//
//    /**
//     * 机构A分摊利润
//     */
//    @ApiModelProperty(value = "机构A分摊利润",required=false)
//    @ExcelProperty(value = "机构A分摊利润")
//    @ColumnWidth(25)
//    private BigDecimal aprofitAmount;
//
//
//    /**
//     * 机构B分摊利润
//     */
//    @ApiModelProperty(value = "机构B分摊利润",required=false)
//    @ExcelProperty(value = "机构B分摊利润")
//    @ColumnWidth(25)
//    private BigDecimal bprofitAmount;
//
//    /**
//     * 校验错误信息
//     */
//    @ApiModelProperty(value = "校验错误信息",required=false)
//    @ColumnWidth(50)
//    @ExcelProperty(value = "校验错误信息")
//    @ExcelIgnore
//    private String errorInfo;
//
//    @ApiModelProperty(value = "比较字段请求时无需填写",required=false)
//    @ExcelIgnore
//    private String compareColumn;
//
//    @ApiModelProperty(value = "数据标识",required=false)
//    @ExcelIgnore
//    private Boolean dataFlag;
//
//    @ApiModelProperty(value = "主键",required=false)
//    @ExcelIgnore
//    private BigDecimal id;
//
//}
