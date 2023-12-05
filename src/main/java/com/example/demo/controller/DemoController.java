package com.example.demo.controller;

import com.example.demo.aop.aspect.NoRepeatSubmit;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.service.demo.DemoProductService;
import com.example.demo.service.demo.IDemoOrderItemService;
import com.example.demo.service.demo.IDemoOrderService;
import com.example.demo.utility.RepeatPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {


    private DemoProductService demoProductService;


    @Autowired
    private IDemoOrderService demoOrderService;

    @Autowired
    private IDemoOrderItemService demoOrderItemService;
    @Autowired
    public DemoController(DemoProductService demoProductService)
    {
        this.demoProductService=demoProductService;
    }
    @GetMapping("/demoProductTest")
//    @RepeatPermission
    @NoRepeatSubmit
    public void demoProductTest()
    {
        demoProductService.test();
    }

    @GetMapping("")
    public DemoProduct getDemoProduct()
    {
        return demoProductService.selectByPrimaryKey(1);
    }
    @GetMapping("/batchInsertDemoOrder")
    public void batchInsertDemoOrder()
    {
         demoOrderService.batchInsertSession();
    }

    @GetMapping("/batchInsertDemoOrderItem")
    public void batchInsertDemoOrderItem()
    {
        demoOrderItemService.batchInsertSession();
    }

}
