package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.scheduling.annotation.Async;
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

    @Resource
    private UserService userService;

    @GetMapping("/asyncFun")
    public void asyncFun() {
        userService.asyncFun();
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
