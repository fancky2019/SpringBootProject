package com.example.demo.service;

import com.example.demo.dao.OrderDetailMapper;
import com.example.demo.dao.OrderMapper;
import com.example.demo.model.entity.Order;
import com.example.demo.model.entity.OrderDetail;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.OrderManagerVM;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class OrderManagerService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Transactional
//    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> addOrder(OrderManagerVM orderManagerVM) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
            Order order = new Order();
            order.setGuid(UUID.randomUUID().toString());
            order.setOrdernumber(orderManagerVM.getOrderNumber());
            order.setCreatetime(new Timestamp(System.currentTimeMillis()));
            order.setOrdertype(1);
            order.setStatus(Short.parseShort("1"));
            Integer result = orderMapper.insert(order);
            if (result <= 0) {
                messageResult.setMessage("保存失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            //获取刚插入的订单，生成的订单ID
            Integer orderId = order.getId();
//            orderManagerVM.getProductOrderDetail().forEach(p ->
            for (OrderDetail p : orderManagerVM.getProductOrderDetail()) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderid(orderId);
                orderDetail.setGuid(UUID.randomUUID().toString());
                orderDetail.setProductid(p.getProductid());
                orderDetail.setCount(p.getCount());
                orderDetail.setDealprice(p.getDealprice());
                orderDetail.setStatus(Short.parseShort("1"));
                Integer re = orderDetailMapper.insert(orderDetail);
                if (re <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    messageResult.setMessage("保存失败");
                    messageResult.setSuccess(false);
                    return messageResult;
                }
            }
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } finally {
            return messageResult;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> deleteOrder(Order order) {
        MessageResult<Void> messageResult = new MessageResult<>();
        try {
//                OrderDetail orderDetail=new OrderDetail();
//                orderDetail.setOrderid(order.getId());
//                List<OrderDetail> orderDetailList=orderDetailMapper.select(orderDetail);
            Integer result = orderMapper.deleteByPrimaryKey(order.getId());
            if (result <= 0) {
                messageResult.setMessage("删除失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            Integer re = orderDetailMapper.deleteByOrderId(order);
            if (re <= 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                messageResult.setMessage("删除失败");
                messageResult.setSuccess(false);
                return messageResult;
            }
            messageResult.setSuccess(true);
        } catch (Exception ex) {
            messageResult.setMessage(ex.getMessage());
            messageResult.setSuccess(false);
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        } finally {
            return messageResult;
        }
    }


}
