package com.example.demo.controller;

import com.example.demo.model.entity.User;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    //获取配置文件的值
    @Value("${spring.datasource.username}")
    private String username;

 private static Logger logger = LogManager.getLogger(UserController.class);
   // private static Logger logger = LogManager.getLogger("bussiness");

//  @RequestMapping("/getUser") 通用
    //SpringMVC的自动装箱（实体类接收参数）
    //get请求：url  传参
    //@RequestMapping("/getUser")
//     @RequestMapping(value = "/getUser",method = RequestMethod.GET)
    @GetMapping("/getUser")
    public User getUser( User user) {
//        User user = new User();
//        user.setName("test");
//        return user;


        logger.info("dssdsdsd");
        logger.error("dssdsdsd");
        return userService.selectByPrimaryKey(user.getId());
    }

    // SpringMVC的自动装箱（实体类接收参数）
    //post提交
   // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
   @PostMapping("/addUser")
    public void addUser(@RequestBody User user)  {
        try {
            int a = user.getId();
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }


}

