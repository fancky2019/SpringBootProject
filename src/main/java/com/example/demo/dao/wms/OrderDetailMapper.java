package com.example.demo.dao.wms;

import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.entity.wms.OrderDetail;

import java.util.List;

public interface OrderDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    // OrderDetail selectByPrimaryKey(Integer id);
    List<OrderDetail> select(OrderDetail orderDetail);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

    int deleteByOrderId(Order order);
}