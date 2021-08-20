package com.example.demo.service.microservice.eurekaclient;

import com.example.demo.controller.UserController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/*
 框架2.1.1升级到2.5.4
 变更记录：feign.hystrix.FallbackFactory--->org.springframework.cloud.openfeign.FallbackFactory;
 */

/*
如果页面显示异常信息，说明熔断没有开启成功
成功：返回UserServiceFallBackFactory的返回值
 */
@Component
public class UserServiceFallBackFactory implements FallbackFactory<UserService> {

    private static Logger logger = LogManager.getLogger(UserController.class);

    @Override
    public UserService create(Throwable throwable) {
//        return (name) ->
//        {
//            String errorMessage = throwable.getMessage();
//            return "FeignClient微服务调用熔断：返回异常默认值";
//        };

        return new UserService() {
            @Override
            public String home(String name) {
                //
                System.out.println(throwable.getMessage());
                logger.error(throwable.getMessage());
                return "返回异常默认值";
            }

            @Override
            public Integer getCount(String name) {
                System.out.println(throwable.getMessage());
                logger.error(throwable.getMessage());
                return 0;
            }

        };

    }

}
