package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.Person;
import com.example.demo.service.UserService;
import com.example.demo.service.test.InterfaceTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/*
  ResponseEntity ：标识整个http相应：状态码、头部信息、响应体内容(spring)
  查询： ResponseEntity.ok(list);
  增删改： ResponseEntity.ok().build(); ResponseEntity.ok(添加成功")"
 */


//调试测Controller注意切换配置文件里的数据库配置:数据库RABC


//@RestController:@RestController注解，相当于@Controller+@ResponseBody两个注解的结合
//                 @Controller 返回传统视图 html
//                @ResponseBody 返回json或xml格式
//

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
    @Qualifier("typeAlia")
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


    /*
     */


    /*
     *建议用@Service加别名
     *如果类用@Component做注解而不是@Service，就不用了指定别名。
     * 如果用@Service注解，多个类实现接口时候就要指定别名：声明@Service("InterfaceTestImpB")，调用 @Autowired、 @Qualifier("InterfaceTestImpA")
     */

    @Autowired
    @Qualifier("InterfaceTestImpA")
    InterfaceTest interfaceTestImpA;

    @Autowired
//    @Qualifier("InterfaceTestImpB")
    InterfaceTest interfaceTestImpB;


    //获取配置文件的值
    @Value("${spring.datasource.username}")
    private String username;
    //
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
    //post提交 data:{}是一个对象，要用对象接收，类的访问级别是共有，否则MVC反射找不到报。
    //  @ResponseBody  返回业务对象，不要返回字符串，不然前台无法转换JSON而报错，还要Json 序列化操作。
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

    //http://localhost:8081/login?name=fancky&password=pas
    @GetMapping("/login")
    @ResponseBody
    public String login(String name, String password) {
        return MessageFormat.format("{0}:{1}", applicationName, name + "," + password);
    }

    //传统URL格式
    //请求格式：http://localhost:8081/user/getURlParameters1?name=getURlParameters1&password=pas
    @GetMapping("/getURlParameters1")
    @ResponseBody
    public String getURlParameters1(@RequestParam("name") String name, @RequestParam(value = "password") String password) {
        return MessageFormat.format("{0}:{1}", applicationName, name + "," + password);
    }


    //restful 风格格式：
    //请求格式：http://localhost:8081/user/getURlParameters2/getURlParameters2/pas
    @GetMapping("/getURlParameters2/{name}/{password}")//注：参数占位符中的名称要和形参的名称一样，否则无法赋值
    @ResponseBody
    public String getURlParameters2(@PathVariable(value = "name") String name, @PathVariable(value = "password") String password) {
        return MessageFormat.format("{0}:{1}", applicationName, name + "," + password);
    }


    //自动生成实体对象
    //http://localhost:8081/user/getPerson/fancky/2
    //SpringBootProject:fancky,2
    @GetMapping("/getPerson/{name}/{age}")//注：参数占位符中的名称要和形参的名称一样，否则无法赋值
    @ResponseBody
    public String getPerson(Person person) {
        return MessageFormat.format("{0}:{1}", applicationName, person.getName() + "," + person.getAge());
    }

    //不能在参数前加注解@RequestParam或@RequestBody
    //自动生成实体对象
    //http://localhost:8081/user/getPerson/fancky/2
    //SpringBootProject:fancky,2
    @GetMapping("/getPersonRequestParam")//注：参数占位符中的名称要和形参的名称一样，否则无法赋值
    @ResponseBody
    public String getPersonRequestParam(Person person, HttpServletRequest request,HttpServletResponse response) {

//         org.apache.catalina.connector.RequestFacade
//        public class RequestFacade implements HttpServletRequest
//         org.apache.catalina.connector.ResponseFacade
//        public class ResponseFacade implements HttpServletResponse
                //请求头
        String value = request.getHeader("Content-Type");
        List<String> headerNames = Collections.list(request.getHeaderNames());
        return MessageFormat.format("{0}:{1}", applicationName, person.getName() + "," + person.getAge());
    }

    @GetMapping("/autowiredTest")
    @ResponseBody
    public String autowiredTest() {
        return this.interfaceTestImpA.fun() + ":" + interfaceTestImpB.fun();
    }
}

