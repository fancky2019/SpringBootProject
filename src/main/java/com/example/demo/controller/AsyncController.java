package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
作用于业务层（service）,service 层方法用async注解。
 */
@RestController
@RequestMapping("/async")
public class AsyncController {


    /*
    @EnableAsync     //启用异步 启动类添加
     */

    /*
    异步请求与异步调用的区别
    两者的使用场景不同，异步请求用来解决并发请求对服务器造成的压力，从而提高对请求的吞吐量；而异步调用是用来做一些非主线流程且不需要实时计算和响应的任务，比如同步日志到kafka中做日志分析等。

    异步请求是会一直等待response相应的，需要返回结果给客户端的；而异步调用我们往往会马上返回给客户端响应，完成这次整个的请求，至于异步调用的任务后台自己慢慢跑就行，客户端不会关心。
     */

    /*
    spring boot 测试发现默认最大线程池数量8
     */

//spring  默认创建线程池类 ThreadPoolTaskExecutor  待确认。


    @Resource
    private UserService userService;

    @GetMapping("/asyncFun")
    public void asyncFun() {
      //  spring boot 测试发现默认最大线程池数量8
        for(int i=0;i<50;i++)
        {
            userService.asyncFun();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @GetMapping("/asyncFunReturn")
    public String asyncFunReturn() throws InterruptedException, ExecutionException {
        return userService.asyncFunReturn().get();
    }

    @GetMapping("/syncFun")
    public String syncFun() {
        return userService.syncFun();
    }
}
