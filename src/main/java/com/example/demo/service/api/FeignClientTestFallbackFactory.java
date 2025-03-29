package com.example.demo.service.api;

import com.example.demo.controller.UserController;
import com.example.demo.model.entity.newclassadmin.UserInfo;
import com.example.demo.model.wmsservicemodel.ShipOrderInventoryDetailDto;
import com.example.demo.service.microservice.eurekaclient.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.engine.identity.UserQuery;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/*
 框架2.1.1升级到2.5.4
 变更记录：feign.hystrix.FallbackFactory--->org.springframework.cloud.openfeign.FallbackFactory;
 */

/*
如果页面显示异常信息，说明熔断没有开启成功
成功：返回UserServiceFallBackFactory的返回值
 */
@Component
@Slf4j
public class FeignClientTestFallbackFactory implements FallbackFactory<FeignClientTest> {

//    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Override
    public FeignClientTest create(Throwable throwable) {
//        return (name) ->
//        {
//            String errorMessage = throwable.getMessage();
//            return "FeignClient微服务调用熔断：返回异常默认值";
//        };

        return new FeignClientTest() {
            @Override
            public String completeShipOrder(BigInteger shipOrderId, String token) {
                return "";
            }

            @Override
            public boolean checkRelation(ShipOrderInventoryDetailDto query, String token) {
                return false;
            }


            @Override
            public String getUser(String name) {
                return "";
            }

            @Override
            public String getMinorDept(long userId, String token) {
                return "";
            }

            @Override
            public String addUser(UserInfo userInfo) {
                return "";
            }

            @Override
            public String addUser1(UserInfo request, String token) {
                return "";
            }


        };

    }

}