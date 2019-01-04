package com.example.demo.Scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestJob {

    /**
     * spring 会自动执行
     */
    @Scheduled(cron="*/5 * * * * ?")
    public  void update()
    {
        System.out.println("每五秒执行一次！");
    }

    @Scheduled(cron="*/10 * * * * ?")
    public  void insert()
    {
        System.out.println("每10秒执行一次！");
    }
}
