package com.example.demo.controller;

import com.example.demo.kafka.KafkaTest;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//测试时候把kafka相关类的@Component注解放开
@RestController
@RequestMapping("/kafka")
public class KafkaController {
    //  @Autowired
    //   private KafkaTest kafkaTest;

    @GetMapping("")
    public MessageResult<Void> kafkaTest() {
        try {
            // kafkaTest.test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
