package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbitMQ")
public class RabbitMQController {

    @Autowired
    private RabbitMQTest rabbitMQTest;

    @GetMapping("")
    public MessageResult<Void> redisTest() {
        try {
            rabbitMQTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
