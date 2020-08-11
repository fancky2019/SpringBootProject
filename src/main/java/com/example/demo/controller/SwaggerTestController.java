package com.example.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//value:不设置tags，前端一级导航菜单显示,tags：前端一级导航菜单显示名称。就不显示Value值。
@Api(value = "Swagger测试",tags = "SwaggerTestController测试")
@RestController
@RequestMapping("/api/SwaggerTest")
public class SwaggerTestController {

    /*
      @ApiOperation:接口描述。
     */




    //value：前端二级导航菜单名称，notes = 文档tab页的"接口描述
    @ApiOperation(value = "getMethodTest",notes = "方法说明：获取后台数据")
    //method 不写的话，默认GET、POST都支持，根据前端方式自动适应。
//    @RequestMapping(value = "/testMethod")
    @RequestMapping(value = "/getMethodTest", method = RequestMethod.GET)
    public String getMethodTest() {
        return "SwaggerTest";
    }

    @ApiOperation(value = "postMethodTest",notes = "方法说明：提交数据")
    //method 不写的话，默认GET、POST都支持，根据前端方式自动适应。
//    @RequestMapping(value = "/testMethod")
    @RequestMapping(value = "/postMethodTest", method = RequestMethod.POST)
    public String postMethodTest() {
        return "SwaggerTest";
    }



    //没有 @ApiOperation注解，前端不显示“接口描述”。
    /**
     * 新增信息
     * @return
     */
    @RequestMapping(value = "/addMethodTest", method = RequestMethod.POST)
    public String addMethodTest() {
        return "SwaggerTest";
    }
}
