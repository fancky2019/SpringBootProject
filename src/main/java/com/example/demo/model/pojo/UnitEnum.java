package com.example.demo.model.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Jackson对枚举进行序列化,默认输出枚举的String名称。名字要对应，区分大小写。如:Zhi
 * 前端传枚举成员名称（注：不能加双引号）给枚举字段。
 */
public enum UnitEnum {
    Zhi(0),
    Tou(1) {
        //重写toString() 方法
        public String toString() {

            return "TOU";
        }
    },
    Ge(2);

    private int value;

    private UnitEnum(int value) {
        this.value = value;
    }

    public UnitEnum fromString(String str) {
        UnitEnum.valueOf(str.toUpperCase());
        return valueOf(str.toUpperCase());
    }

//    //JsonVale：序列化时 枚举对应生成的值:0或1
//    @JsonValue
//    public int getValue() {
//        return this.value;
//    }
//
//    //JsonCreator ：反序列化时的 初始化函数，入参为 对应该枚举的 json值
//    @JsonCreator
//    public static UnitEnum getItem(int value) {
//        //values= MessageType.values()
//        for (UnitEnum item : UnitEnum.values()) {
//            if (item.getValue() == value) {
//                return item;
//            }
//        }
//        return null;
//    }

}
