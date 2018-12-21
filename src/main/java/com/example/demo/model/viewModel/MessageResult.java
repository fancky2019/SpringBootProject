package com.example.demo.model.viewModel;

import java.util.List;

public class MessageResult<T> {

    /**
     * 执行结果（true:成功，false:失败）
     */
    private Boolean success;
    private String message;
    private List<T> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
