package com.example.demo.controller.rabc;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/*
RABC权限管理项目
Swagger: http://localhost:8080/swagger-ui.html
 */
@Controller
@RequestMapping("rabc/userManager")
public class UserManagerController {

    private static Logger logger = LogManager.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @GetMapping("/getUser")
    @ResponseBody//当使用@Controller返回数据必须要加上@ResponseBody
    public Users getUser(Users user) {


        Users re = userService.selectByPrimaryKey(user.getId());
        return re;
    }

    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUser")
    @ResponseBody
    public void addUser(@RequestBody Users user) {
        try {
            int a = user.getId();
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }
}
