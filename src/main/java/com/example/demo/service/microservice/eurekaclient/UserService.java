package com.example.demo.service.microservice.eurekaclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


//指定服务名称：Instances currently registered with Eureka列表里的服务
/*
OpenFeign:服务之间的调用，集成了Ribbon和Hystrix。
不停刷新链接调用会发现负载均衡


如果页面显示异常信息，说明熔断没有开启成功

如果不想使某些方法进行熔断，可以单独再写一个接口。




仪表盘：浏览器输入：http://localhost:8081/hystrix
        页面http输入框输入：http://localhost:8081/hystrix.stream
        点击Monitor Stream 按钮
        运行Jmeter
 */
//@FeignClient("server")//不开启熔断的回调
//server 只能一个服务指定。
@FeignClient(value = "server", fallbackFactory=UserServiceFallBackFactory.class)//开启回调
public interface UserService {
    @RequestMapping(value = "/user")
    String home(@RequestParam String name);

    @RequestMapping(value = "/getCount")
    Integer getCount(@RequestParam String name);
}
