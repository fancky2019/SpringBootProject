package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.rabbitMQ.RabbitMqManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbitMQ")
public class RabbitMQController {

    @Autowired
    private RabbitMQTest rabbitMQTest;

    @Autowired
    private RabbitMqManager rabbitMqManager;

    @GetMapping("")
    public MessageResult<Void> rabbitMQTest() {
        try {
            rabbitMQTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    @GetMapping("/getMessageCount")
    public MessageResult<Void> getMessageCount(String queueName) {
        try {
            rabbitMQTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}
