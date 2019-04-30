package com.example.demo.model.viewModel;

import java.util.List;

public class PageData<T> {
    private Integer count;
    private List<T>  data;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
