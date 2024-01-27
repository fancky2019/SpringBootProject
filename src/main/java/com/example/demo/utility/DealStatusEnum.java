package com.example.demo.utility;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 */
public enum DealStatusEnum {
    /**
     *
     */
    UN_DEAL(0, "未处理"),
    DEALING(1, "处理中"),
    //    Tou(1,"头") {
//        //重写toString() 方法
//        public String toString() {
//
//            return "TOU";
//        }
//    },
    COMPLETE(2, "完成");

    private  int value;
    private  String description;


    public void setValue(int value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }




    public static String getDescription(int value) {
        DealStatusEnum[] values = values();
        for (DealStatusEnum DealStatusEnum : values) {
            if (DealStatusEnum.value ==value) {
                return DealStatusEnum.description;
            }
        }
        return "Unknown value";
    }

    //jackson序列化

    //JsonVale：序列化时 枚举对应生成的值:0或1
    @JsonValue
    public int getValue() {
        return this.value;
    }

    //JsonCreator ：反序列化时的 初始化函数，入参为 对应该枚举的 json值
    /**
     *
     * @param value
     * @return
     */
    @JsonCreator
    public static DealStatusEnum getDealStatusEnum(int value) {
        //values= MessageType.values()
        for (DealStatusEnum item : DealStatusEnum.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }


    private DealStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 名称要对应  如：Zhi
     * @param str
     * @return
     */
    public static DealStatusEnum fromString(String str) {
        try {
            //名称要对应  如：Zhi
            return DealStatusEnum.valueOf(str);
        }
        catch (Exception ex)
        {
            String msg= ex.getMessage();
            return  null;
        }

    }
}