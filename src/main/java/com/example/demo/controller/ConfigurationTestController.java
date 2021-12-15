package com.example.demo.controller;

import com.example.demo.model.config.ConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configuration")
public class ConfigurationTestController {

    @Autowired
    private ConfigModel configModel;

    @GetMapping(value = "/configurationTest")
    public String configurationTest() {
        int m = 0;
        return configModel.getAddress();
    }
}
