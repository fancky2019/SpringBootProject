package com.example.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "多方法",tags = "相同URL")
@RestController
@RequestMapping("/api/SwaggerTest")
public class SwaggerTestController {

    @ApiOperation(value = "相同url,不同method",notes = "针对相同url，不同mehtod类型未展示bug")
    @RequestMapping(value = "/allMethod")
    public String allMethod(){
        return "SwaggerTest";
    }
}
