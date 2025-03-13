package com.example.demo.model.request;

import com.example.demo.model.pojo.Page;
import lombok.Data;

@Data
public class MqMessageRequest extends EntityBaseRequest {
    private String msgId;
    private String msgContent;
    private String exchange;
    private String routeKey;
    private String queue;
    private Integer status;
    private String remark;
}
