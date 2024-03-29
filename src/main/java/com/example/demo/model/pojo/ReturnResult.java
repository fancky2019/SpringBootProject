package com.example.demo.model.pojo;

import lombok.Data;
import org.slf4j.MDC;

import java.io.Serializable;

@Data
public class ReturnResult<T> implements Serializable {
    private static final long serialVersionUID = -2854213490241311256L;

    private static final String REQUEST_ID_KEY = "traceId";

    private Boolean success = true;
    private Integer code;//状态码
    private String message; //结果描述
    private T data; //返回结果,对象或者数组
    private String requestId = MDC.get(REQUEST_ID_KEY);//请求id：traceId

}
