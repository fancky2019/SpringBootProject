package com.example.demo.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Setter;

/**
 *
 */
public enum ProductTestStatusEnum implements EnumOperation{
    /**
     *
     */
    EXCEPTION(0, "异常"),
    /**
     *
     */
    NORMAL(1, "正常"),
    //    Tou(1,"头") {
//        //重写toString() 方法
//        public String toString() {
//
//            return "TOU";
//        }
//    },
    /**
     *
     */
    INVALID(2, "失效");

    private  int value;
    private  String description;


    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    private ProductTestStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 名称要对应  如：Zhi
     * @param str
     * @return
     */
    public static ProductTestStatusEnum fromString(String str) {
        try {
            //名称要对应  如：Zhi
            return ProductTestStatusEnum.valueOf(str);
        }
        catch (Exception ex)
        {
            String msg= ex.getMessage();
            return  null;
        }

    }

    public static   String getDescription(int value) {
        ProductTestStatusEnum[] values = values();
        for (ProductTestStatusEnum unitEnum : values) {
            if (unitEnum.value ==value) {
                return unitEnum.description;
            }
        }
        return "Unknown value";
    }

    //jackson序列化

    //JsonVale：序列化时 枚举对应生成的值:0或1
    @Override
    @JsonValue
    public int getValue() {
        return this.value;
    }



    /**
     * JsonCreator ：反序列化时的 初始化函数，入参为 对应该枚举的 json值
     * @param value
     * @return
     */
    @JsonCreator
    public static ProductTestStatusEnum getUnitEnum(int value) {
        //values= MessageType.values()
        for (ProductTestStatusEnum item : ProductTestStatusEnum.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

}
