package com.example.demo.controller;

import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.service.demo.DemoProductService;
import com.example.demo.service.demo.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private DemoProductService demoProductService;


    @Autowired
    public DemoController(DemoProductService demoProductService)
    {
        this.demoProductService=demoProductService;
    }

    @GetMapping("")
    public DemoProduct getDemoProduct()
    {
        return demoProductService.selectByPrimaryKey(1);
    }

}
