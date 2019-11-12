package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.HashMap;

//调试测Controller注意切换配置文件里的数据库配置:数据库RABC


//@RestController:@RestController注解，相当于@Controller+@ResponseBody两个注解的结合
//用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面
@Controller
@RequestMapping("/user")
public class UserController {

    /*
     springboot 的Bean 来源： 1）@Repository @Component @Service @Controller,spring自动扫描这些类并注册成Bean。
                              2）@Configuration 配置文件类里的Bean。
     */
     /*
    Bean同一类型的不同实例，加Bean(实例)的别名
    @Qualifier 注解的Bean不同类型的实例,通过在类型名称上加名称(@Service("UserService"))，用@Qualifier("typeAlia")区分
     */
//    @Resource("UserService")
    @Resource
    private UserService userService;

    /*
    @Resource:默认按照ByName自动注入，由J2EE提供，需要导入包javax.annotation.Resource。@Resource有两个重要的属性：name和type，
    而Spring将@Resource注解的name属性解析为bean的名字，而type属性则解析为bean的类型。所以，如果使用name属性，则使用byName的自动注入策略，
    而使用type属性时则使用byType自动注入策略。如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略。

    @Autowired:spring框架提供的，是按照类型（byType）装配依赖对象，默认情况下它要求依赖对象必须存在，如果允许null值，可以设置它的required属性为false。
    如果我们想使用按照名称（byName）来装配，可以结合@Qualifier注解一起使用。
     @Autowired 优先加载@Primary注解的。
     */
//    @Autowired
//    private UserService userService1;
//
//
//    @Autowired
//    @Qualifier("baseDao")//指定别名--当多个同一对象的Bean
//    private UserService userService2;


    //获取配置文件的值
    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.application.name}")
    private String applicationName;


    private static Logger logger = LogManager.getLogger(UserController.class);
    // private static Logger logger = LogManager.getLogger("business");


    //http://localhost:8080/user/getUser?id=1
    //  @RequestMapping("/getUser") 通用
    //SpringMVC的自动装箱（实体类接收参数）
    //get请求：url  传参
    //@RequestMapping("/getUser")
    //如果不指定方法，会出现;GET、HEAD、POST、PUT、DELETE、OPTIONS、PATCH方法。
    //@RequestMapping(value = "/getUser",method = RequestMethod.GET)
    @GetMapping("/getUser")
    @ResponseBody//当使用@Controller返回数据必须要加上@ResponseBody
    public Users getUser(Users user) {
//        User user = new User();
//        user.setName("test");
//        return user;


        logger.info("dssdsdsd");
        logger.error("dssdsdsd");
        Users re = userService.selectByPrimaryKey(user.getId());
        return re;
    }

    // SpringMVC的自动装箱（实体类接收参数）
    //post提交
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

    @GetMapping("/log")
    @ResponseBody
    public String log(String str) {
        return MessageFormat.format("{0}:{1}", applicationName, str);
    }

    //http://localhost:8080/login?name=fancky&password=pas
    @GetMapping("/login")
    @ResponseBody
    public String login(String name, String password) {
        return MessageFormat.format("{0}:{1}", applicationName, name + "," + password);
    }
}

