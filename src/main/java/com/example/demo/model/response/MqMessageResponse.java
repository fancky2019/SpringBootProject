package com.example.demo.model.response;

import com.example.demo.model.entity.demo.EntityBase;
import lombok.Data;

@Data
public class MqMessageResponse extends EntityBase {
    private String msgId;
    private String msgContent;
    private String exchange;
    private String routeKey;
    private String queue;
    private Integer status;
    private String remark;
}
