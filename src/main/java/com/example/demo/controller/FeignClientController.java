package com.example.demo.controller;

import com.example.demo.model.entity.newclassadmin.UserInfo;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.service.api.FeignClientTest;
import com.example.demo.service.microservice.eurekaclient.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/*
    openfeign  http 客户单端,通过配置启用OkHttp代替默认的http
    参见SpringCloud 项目
     微服务注册到注册中心的名称一样，网关路由转发会采用负载均衡
     #会用自带的负载均衡：http://localhost:8080/gateway1/user?name=a
     # 客户端负载均衡实现：两个微服务注册到注册中心的名称一样，ip和端口不一样
 */
@RestController
@RequestMapping("/feignclient")
public class FeignClientController {
    //@Resource的作用相当于@Autowired。
    //@Resource:如果既不制定name也不制定type属性，这时将通过反射机制使用byName自动注入策略
    //@Autowired:按照byType自动注入。

   /*
      Client 接口默认方法execute
      HttpURLConnection connection = this.convertAndSend(request, options);

      OkHttpClient
      execute
     */

    /**
     * 其他微服务调用：
     * Service 层封装其他微服务的调用。通过Feign指定注册中心的微服务的名称进行调用。
     * 通过微服务调用熔断：重写整个服务接口，进行熔断处理
     */
    //  @Resource  //java 的 IOC
    @Autowired    //spring mvc 的IOC
    private UserService userService;

    @Autowired
    private FeignClientTest feignClientTest;


    @GetMapping("")
    public String test(String name) {
        return userService.home(name);
    }

    /**
     * 调用注册中心
     *
     * @param name
     * @return
     */
    //http://localhost:8081/feignclient/testParam?name=fanckyTest1
    @RequestMapping("/testParam")
    public String testParam(@RequestParam String name) {
        String re = userService.home(name);
        return re;
    }


    // region 直接调用URL


    /**
     * 直接调用URL
     *
     * @return
     */
    @RequestMapping("/callOtherServiceByUrl")
    public String callOtherServiceByUrl() {
        String jsonStr = feignClientTest.getUser("test");
        return jsonStr;
    }


    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String addUser(UserInfo userInfo) {

        userInfo.setUserName("test");
        String jsonStr = feignClientTest.addUser(userInfo);
        return jsonStr;
    }


    @GetMapping("/getUserInfo")
    public String getUserInfo(String name) {
        return "getUserInfo - "+name;
    }
    //endregion
}
