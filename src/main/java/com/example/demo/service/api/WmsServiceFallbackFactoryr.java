package com.example.demo.service.api;

import com.example.demo.controller.UserController;
import com.example.demo.model.wmsservicemodel.ShipOrderInventoryDetailDto;
import com.example.demo.service.microservice.eurekaclient.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;

/*
 框架2.1.1升级到2.5.4
 变更记录：feign.hystrix.FallbackFactory--->org.springframework.cloud.openfeign.FallbackFactory;
 */

/*
如果页面显示异常信息，说明熔断没有开启成功
成功：返回UserServiceFallBackFactory的返回值
 */
@Component
public class WmsServiceFallbackFactoryr implements FallbackFactory<WmsService> {

    private static Logger logger = LogManager.getLogger(UserController.class);

    @Override
    public WmsService create(Throwable throwable) {
//        return (name) ->
//        {
//            String errorMessage = throwable.getMessage();
//            return "FeignClient微服务调用熔断：返回异常默认值";
//        };

        return new WmsService() {

            @Override
            public String completeShipOrder( BigInteger shipOrderId, String token) {
                //
                System.out.println(throwable.getMessage());
                logger.error(throwable.getMessage());
                return "返回异常默认值";
            }

            @Override
            public String shipOrderTest(@RequestParam String test) {
                System.out.println(throwable.getMessage());
                logger.error(throwable.getMessage());
                return "0";
            }

            @Override
            public boolean checkRelation(ShipOrderInventoryDetailDto query, String token) {
                return false;
            }

        };

    }

}