package com.example.demo.controller;

import com.example.demo.quartz.QuartzJobComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Autowired
    QuartzJobComponent quartzJobComponent;
    Random random = new Random(1);

    @GetMapping(value = "/addjob")
    public String addJob(int ruleId) {

//        int ruleId = random.nextInt(10000);
        quartzJobComponent.addJob(ruleId);
        return "error11111";
    }

    @GetMapping(value = "/removejob")
    public String removeJob(int ruleId) {
        quartzJobComponent.removeJob(ruleId);
        return "error11111";
    }
}
