package com.example.demo.dao.wms;

import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.entity.wms.OrderDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
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