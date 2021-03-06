package com.example.demo.model.viewModel;

import java.io.Serializable;
import java.util.List;

public class MessageResult<T> implements Serializable {

    /**
     * 执行结果（true:成功，false:失败）
     */
    private Integer code;
    private Boolean success;
    private String message;
    private T data;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
