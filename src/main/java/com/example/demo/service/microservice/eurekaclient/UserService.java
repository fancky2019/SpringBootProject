package com.example.demo.service.microservice.eurekaclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


//指定服务名称：Instances currently registered with Eureka列表里的服务
/*
OpenFeign:服务之间的调用，集成了Ribbon和Hystrix。
不停刷新链接调用会发现负载均衡


如果页面显示异常信息，说明熔断没有开启成功
 */
//@FeignClient("server")
@FeignClient(value = "server", fallbackFactory=UserServiceFallBackFactory.class)
public interface UserService {
    @RequestMapping(value = "/user")
    String home(@RequestParam String name);
}
