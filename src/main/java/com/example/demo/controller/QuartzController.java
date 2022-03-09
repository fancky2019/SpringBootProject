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

//    2）Cron表达式范例：
//    每隔5秒执行一次：*/5 * * * * ?
//    每隔1分钟执行一次：0 */1 * * * ?
//    每天23点执行一次：0 0 23 * * ?
//    每天凌晨1点执行一次：0 0 1 * * ?
//    每月1号凌晨1点执行一次：0 0 1 1 * ?
//    每月最后一天23点执行一次：0 0 23 L * ?
//    每周星期天凌晨1点实行一次：0 0 1 ? * L
//    在26分、29分、33分执行一次：0 26,29,33 * * * ?
//    每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?


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
