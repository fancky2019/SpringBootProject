package com.example.demo.controller;

import com.example.demo.model.entity.User;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

//调试测Controller注意切换配置文件里的数据库配置:数据库Test


//@RestController:@RestController注解，相当于@Controller+@ResponseBody两个注解的结合
//用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    //获取配置文件的值
    @Value("${spring.datasource.username}")
    private String username;

    private static Logger logger = LogManager.getLogger(UserController.class);
    // private static Logger logger = LogManager.getLogger("business");


    //http://localhost:8080/user/getUser?id=1
    //  @RequestMapping("/getUser") 通用
    //SpringMVC的自动装箱（实体类接收参数）
    //get请求：url  传参
    //@RequestMapping("/getUser")
    //@RequestMapping(value = "/getUser",method = RequestMethod.GET)
    @GetMapping("/getUser")
    @ResponseBody//当使用@Controller返回数据必须要加上@ResponseBody
    public User getUser(User user) {
//        User user = new User();
//        user.setName("test");
//        return user;


        logger.info("dssdsdsd");
        logger.error("dssdsdsd");
        User re = userService.selectByPrimaryKey(user.getId());
        return re;
    }

    // SpringMVC的自动装箱（实体类接收参数）
    //post提交
    // @RequestMapping("/addUser")
    // @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @PostMapping("/addUser")
    @ResponseBody
    public void addUser(@RequestBody User user) {
        try {
            int a = user.getId();
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }

    /**
     * 2.@RestController注解，相当于@Controller+@ResponseBody两个注解的结合，
     * 返回json数据不需要在方法前面加@ResponseBody注解了，
     * 但使用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
     *
     * @param map
     * @return
     */
    @RequestMapping("")
    // @ResponseBody// 就是返回字符串了
    public String index(HashMap<String, Object> map) {
        //返回值给页面
        map.put("hello", "fanckyrrr");
        return "user/index";
    }

    @RequestMapping("/hello")
    public String helloHtml(HashMap<String, Object> map, Model model) {
        model.addAttribute("say", "欢迎欢迎,热烈欢迎");
        map.put("hello", "欢迎进入HTML页面");
        return "index";
    }


}

