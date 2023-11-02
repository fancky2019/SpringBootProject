package com.example.demo.service.api;

import com.example.demo.model.entity.newclassadmin.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Feign 集成了hystrix ，若调试最好在配置文件
 * feign:
 * hystrix:
 * enabled: false
 * <p>
 * 以便将原始的异常信息抛出
 *
 *
 *
 *
 */

//1、引入依赖
//注意springboot和springcloud的版本对应：https://spring.io/projects/spring-cloud
//<!--  FeignClient：注意版本号和其他版本一致-->
//<dependency>
//<groupId>org.springframework.cloud</groupId>
//<!--注意此处的依赖是SpringBoot2.0以后专用的，如果您使用的SpringBoot版本低于2.0请使用spring-cloud-starter-feign-->
//<artifactId>spring-cloud-starter-openfeign</artifactId>
//<version>2.1.0.RELEASE</version>
//</dependency>
//2、启动类@EnableFeignClients//启用feign。微服务之间调用,服务发现
//不能识别服务中有没有请求的路径方法。
@FeignClient(name = "feignClientTest", url = "${sbp.ordermigratedbtoolurl}")
//@FeignClient(value = "single-provider")//注册中心的服务名称
public interface FeignClientTest {

    /**
     * 参数前要加 @RequestParam 或post @RequestBody
     *
     * @param name
     * @return
     */
    @GetMapping("/test/getUser")
    String getUser(@RequestParam String name);


    @GetMapping("/test/getUser")
    String getMinorDept(@RequestParam("userId")long userId, @RequestHeader("token")String token);


//    String getUser(@RequestParam("name") String name);
    /**
     * 参数前要加 @RequestParam 或post @RequestBody
     *
     * @param
     * @return
     */
    @PostMapping("/test/addUser")
    String addUser(@RequestBody UserInfo userInfo);

    @PostMapping("/test/addUser1")
    String addUser1(@RequestBody @Validated UserInfo request, @RequestHeader("token") String token);
}
