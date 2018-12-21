package com.example.demo.controller;

import com.example.demo.model.entity.Order;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.OrderManagerVM;
import com.example.demo.service.OrderManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ordermanager")
public class OrderManagerController {

    @Autowired
//    @Resource
    private OrderManagerService orderManagerService;

    @RequestMapping("/addOrder")
    public MessageResult<Void> addOrder(@RequestBody OrderManagerVM orderManagerVM) {
        return orderManagerService.addOrder(orderManagerVM);
    }

    @PostMapping("/deleteOrder")
    public MessageResult<Void> deleteOrder(@RequestBody Order order) {
        return orderManagerService.deleteOrder(order);
    }

}
