package com.example.demo.service.microservice.eurekaclient;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/*
如果页面显示异常信息，说明熔断没有开启成功
成功：返回UserServiceFallBackFactory的返回值
 */
@Component
public class UserServiceFallBackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
//        return new UserService() {
//            @Override
//            public String home(String name) {
//                //
//                System.out.println(throwable.getMessage());
//                return "返回异常默认值";
//            }
//        };

        return (name) ->
        {
            String errorMessage = throwable.getMessage();
            return "返回异常默认值";
        };
    }

}
