//package com.example.demo.controller;
//
//import com.example.demo.config.ConfigModelProperty;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/configuration")
//public class ConfigurationTestController {
//
//    @Autowired
//    private ConfigModelProperty configModelProperty;
//
//    @GetMapping(value = "/configurationTest")
//    public String configurationTest() {
//        int m = 0;
//        return configModelProperty.getAddress();
//    }
//}
