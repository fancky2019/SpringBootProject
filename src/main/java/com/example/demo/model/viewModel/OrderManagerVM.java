package com.example.demo.model.viewModel;

import com.example.demo.model.entity.wms.OrderDetail;

import java.util.List;

public class OrderManagerVM {
    /**
     * 订单号
     */
    private String orderNumber;

    private List<OrderDetail> productOrderDetail;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderDetail> getProductOrderDetail() {
        return productOrderDetail;
    }

    public void setProductOrderDetail(List<OrderDetail> productOrderDetail) {
        this.productOrderDetail = productOrderDetail;
    }

}
