package com.example.demo.model.viewModel;

import java.util.List;

public class PageData<T> {
    private Long count;
    private List<T> data;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
