package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.service.microservice.eurekaclient.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/feignclient")
public class FeignClientController {
    //@Resource的作用相当于@Autowired。
    //@Resource:如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略
    //@Autowired:按照byType自动注入。


    //  @Resource
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String test(String name) {
        return userService.home(name);
    }

    //http://localhost:8081/feignclient/testParam?name=fanckyTest1
    @RequestMapping("testParam")
    public String testParam(@RequestParam String name) {
        return userService.home(name);
    }

}
