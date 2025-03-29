package com.example.demo.model.wmsservicemodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
公共接口和API参数：一律使用包装类
内部确定非空的方法：可以使用原始类型
持久层实体类：使用包装类

使用 @NotNull 等注解：如果需要确保非空，应使用注解而非原始类型
 */
@Slf4j
@Data
public class ShipOrderInventoryDetailDto {
    private String applyShipOrderCode;
    private String shipOrderCode;
    private String serialNo;

    //类型要使用包装类： 使用 Lombok，确保正确配置：
//    private boolean serialNoMustMatch = false;
    //boolean不指定名称，获取不到前段传的值 ，不加is 可以 即：serialNoMustMatch
    //Jackson 默认会去掉 is 前缀来匹配字段名
//    @JsonProperty("isSerialNoMustMatch")
    private Boolean isSerialNoMustMatch;

}
