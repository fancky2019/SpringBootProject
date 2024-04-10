package com.example.demo.easyexcel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Data
@ApiModel(description = "共享业务清单明细导入请求对象")
public class GXDetailListVO implements Serializable {

    /**
     * 保单号
     */
    @ApiModelProperty(value = "保单号",required=false)
    @ExcelProperty(value = "保单号")
    @ColumnWidth(25)
    private String policyNo;

    /**
     * 保单类型
     */
    @ApiModelProperty(value = "保单类型",required=false)
    @ExcelProperty(value = "保单类型")
    @ColumnWidth(25)
//    @DropDownSetField(sourceClass = PolicyTypeDropDownSetImpl.class)
    private String policyType;

    /**
     * 数据来源
     */
    @ApiModelProperty(value = "数据来源",required=false)
    @ExcelProperty(value = "数据来源")
    @ColumnWidth(25)
    @DropDownSetField(sourceClass = DataSourceDropDownSetImpl.class)
    private String dataSource;

    /**
     * 数据状态
     */
    @ApiModelProperty(value = "数据状态",required=false)
    @ExcelProperty(value = "数据状态")
    @ColumnWidth(25)
    @TableField("DATA_STATUS")
//    @DropDownSetField(sourceClass = DataStatusDropDownSetImpl.class)
    private String dataStatus;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态",required=false)
    @ExcelProperty(value = "审核状态")
    @ColumnWidth(25)
    @TableField("AUDIT_STATUS")
//    @DropDownSetField(sourceClass = AuditStatusDropDownSetImpl.class)
    private String auditStatus;

    /**
     * 处理状态
     */
    @ApiModelProperty(value = "处理状态",required=false)
    @ExcelProperty(value = "处理状态")
    @ColumnWidth(25)
    @TableField("HANDLE_STATUS")
//    @DropDownSetField(sourceClass = HandleStatusDropDownSetImpl.class)
    private String handleStatus;

    /**
     * 保单生效时间
     */
    @ApiModelProperty(value = "保单生效时间",required=false)
    @ExcelProperty(value = "保单生效时间")
    @ColumnWidth(25)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("EFFECT_TIME")
    private Date effectTime;

    /**
     * 投保人
     */
    @ApiModelProperty(value = "投保人",required=false)
    @ExcelProperty(value = "投保人")
    @ColumnWidth(25)
    @TableField("POLICY_HOLDER")
    private String policyHolder;

    /**
     * 被保人
     */
    @ApiModelProperty(value = "被保人",required=false)
    @ExcelProperty(value = "被保人")
    @ColumnWidth(25)
    @TableField("INSURED")
    private String insured;

    /**
     * 统保代码
     */
    @ApiModelProperty(value = "统保代码",required=false)
    @ExcelProperty(value = "统保代码")
    @ColumnWidth(25)
    @TableField("TB_CODE")
    private String tbCode;

    /**
     * 签单保费
     */
    @ApiModelProperty(value = "签单保费",required=false)
    @ExcelProperty(value = "签单保费")
//    @NumberFormat(value = "0.00%", roundingMode = RoundingMode.HALF_UP)
    @NumberFormat("0.000_ ")
    @ColumnWidth(25)
    @TableField("PREMIUM")
    private BigDecimal premium;

    /**
     * 保成利润率
     */
    @ApiModelProperty(value = "保成利润率",required=false)
    @ExcelProperty(value = "保成利润率")
    @ColumnWidth(25)
    @TableField("PROFIT_SCALE")
    private BigDecimal profitScale;

    /**
     * 起保时间
     */
    @ApiModelProperty(value = "起保时间",required=false)
    @ExcelProperty(value = "起保时间"/*,converter = EasyExcelLocalDateConverter.class*/)
    @ColumnWidth(25)
    @TableField("START_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 终保时间
     */
    @ApiModelProperty(value = "终保时间",required=false)
    @ExcelProperty(value = "终保时间"/*,converter = EasyExcelLocalDateConverter.class*/)
    @ColumnWidth(25)
    @TableField("END_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 分公司code
     */
    @ApiModelProperty(value = "分公司",required=false)
    @ExcelProperty(value = "分公司")
    @ColumnWidth(25)
    @TableField("BRANCH_CODE")
    private String branchCode;


    /**
     * 分公司
     */
    @ExcelIgnore
    @TableField("BRANCH_NAME")
    private String branchName;

    /**
     * 共享协议号
     */
    @ApiModelProperty(value = "共享协议号",required=false)
    @ExcelProperty(value = "共享协议号")
    @ColumnWidth(25)
    @TableField("GB_AGRT_CODE")
    private String gbAgrtCode;

    /**
     * 机构A分摊保费
     */
    @ApiModelProperty(value = "机构A分摊保费",required=false)
    @ExcelProperty(value = "机构A分摊保费")
    @ColumnWidth(25)
    @TableField("A_SHARE_AMOUNT")
    private BigDecimal aShareAmount;

    /**
     * 机构B分摊保费
     */
    @ApiModelProperty(value = "机构B分摊保费",required=false)
    @ExcelProperty(value = "机构B分摊保费")
    @ColumnWidth(25)
    @TableField("B_SHARE_AMOUNT")
    private BigDecimal bShareAmount;

    /**
     * 机构A分摊利润
     */
    @ApiModelProperty(value = "机构A分摊利润",required=false)
    @ExcelProperty(value = "机构A分摊利润")
    @ColumnWidth(25)
    @TableField("A_PROFIT_AMOUNT")
    private BigDecimal aProfitAmount;


    /**
     * 机构B分摊利润
     */
    @ApiModelProperty(value = "机构B分摊利润",required=false)
    @ExcelProperty(value = "机构B分摊利润")
    @ColumnWidth(25)
    @TableField("B_PROFIT_AMOUNT")
    private BigDecimal bProfitAmount;

    /**
     * 校验错误信息
     */
    @ApiModelProperty(value = "校验错误信息",required=false)
    @ColumnWidth(50)
    @ExcelProperty(value = "校验错误信息")
    @ExcelIgnore
    private String errorInfo;

    @ApiModelProperty(value = "比较字段请求时无需填写",required=false)
    @ExcelIgnore
    private String compareColumn;

}
