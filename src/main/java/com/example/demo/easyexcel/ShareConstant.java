package com.example.demo.easyexcel;

import java.util.Arrays;
import java.util.Optional;

public class ShareConstant {

    public static final String MODULE_CODE_SHARE = "share";

    public static final String ZGS_CODE = "9010100";

    public static final String SHARE_ROLE_ADMIN = "gxadmin";

    public static final String AUDIT_PARAM = "approve";

    public enum GXStatusEnum{

        STATUS_TMP_SAVE("00","暂存"),

        STATUS_VAILD("01","生效"),

        STATUS_GOBACK("02","退回修改"),

        STATUS_PENDING_APPROVAL("03","待审核"),

        STATUS_RECALL("04","撤回");

        private String statusCode;

        private String statusName;

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        GXStatusEnum(String statusCode,String statusName){
            this.statusCode = statusCode;
            this.statusName = statusName;
        }

    }



    public enum GXDelFlagEnum{

        NORMAL("1","正常"),
        DELETED("0","已删除");

        GXDelFlagEnum(String code,String name){
            this.code = code;
            this.name = name;
        }

        private String code;

        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum GXIsGoBackeEnum{
        GOBACKE_TRUE("1","是"),
        GOBACKE_FALSE("0","否");

        GXIsGoBackeEnum(String code,String name){
            this.code = code;
            this.name = name;
        }

        private String code;

        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public enum GXDropDownEnum{
        DROPDOWN_STATUS_WORK("01","审批状态"),
        DROPDOWN_CERT_TYPE("02","证件类型"),
        DROPDOWN_UNIT("03","分公司"),
        DROPDOWN_HANDLE_STATUS("04","清单明细处理状态"),
        DROPDOWN_AUDIT_STATUS("05","清单明细审核状态");

        GXDropDownEnum(String code,String name){
            this.code = code;
            this.name = name;
        }
        private String code;

        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     *共享用户角色
     */
    public enum GXUserRoleConstant{
        APPLY("apply","申请角色"),
        AUDIT1("gxaudit1","分公司复核"),
        AUDIT2("gxaudit2","分公司终审"),
        AUDIT3("gxaudit3","总公司复核/总公司审核"),
        AUDIT4("gxaudit4","总公司终审"),
        GX_ADMIN("gxadmin","管理员");
        private String code;

        private String name;

        GXUserRoleConstant(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 审核状态
     */
    public enum AuditStatusEnum {
        PASS("1","通过"),
        REJECT("0","退回");

        AuditStatusEnum(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 审核状态
     */
    public enum OperateTypeEnum {
        APPLY("申请"),
        AUDIT("审核");

        OperateTypeEnum(String type){
            this.type = type;
        }

        private String type;

        public String getType() {
            return type;
        }
    }


    /**
     * 保单类型
     */
    public enum PolicyType {
        NEWEST("1","最新保单"),
        ORIGINAL("0","原始保单");

        PolicyType(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    /**
     * 数据来源
     */
    public enum DataSource {
        P09("1","P09"),
        IMPORT("2","调入");

        DataSource(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    /**
     * 数据状态
     */
    public enum DataStatus {
        DATA_A("01","未确认"),
        DATA_B("02","已确认"),
        DATA_C("03","调出"),
        DATA_D("04","调入已确认"),
        DATA_E("05","调入未确认");

        DataStatus(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }



    /**
     * 审核状态
     */
    public enum AuditStatus {
        AUDIT_A("01","未处理"),
        AUDIT_B("02","待审核"),
        AUDIT_C("03","审核通过"),
        AUDIT_D("04","已调出"),
        AUDIT_E("05","未通过"),
        AUDIT_F("06","退回"),
        AUDIT_G("07","调出退回");

        AuditStatus(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static AuditStatus getRelativeByCode(String code){
            Optional<AuditStatus> any = Arrays.stream(AuditStatus.values())
                    .filter(i -> i.getCode().equals(code)).findAny();
            if (null == any){
                return null;
            }
            return any.get();
        }
    }



    /**
     * 处理状态
     */
    public enum HandleStatus {
        UNTREATED("0","未处理"),
        PROCESSED("1","已处理");

        HandleStatus(String code, String value){
            this.code = code;
            this.value = value;
        }

        private String code;

        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    /**
     * 流程图定义key
     */
    public final class ProcessKey{
        //共享协议审批
        public static final String GX_WORKFLOW = "gx_workflow";

        //清单审批
        public static final String GX_DETAIL_LIST = "gx_detail_list";
    }
}
