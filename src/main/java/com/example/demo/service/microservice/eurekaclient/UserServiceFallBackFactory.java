package com.example.demo.service.microservice.eurekaclient;

import com.example.demo.controller.UserController;
import feign.hystrix.FallbackFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

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
