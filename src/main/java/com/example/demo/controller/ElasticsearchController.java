package com.example.demo.controller;

import com.example.demo.model.elasticsearch.DemoProduct;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.api.FeignClientTest;
import com.example.demo.service.elasticsearch.ESDemoProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Elasticsearch")
public class ElasticsearchController {

//    @Autowired
//    private ESDemoProductService esDemoProductService;


    private final ESDemoProductService esDemoProductService;


    //构造函数注入
    public ElasticsearchController(ESDemoProductService esDemoProductService) {
        this.esDemoProductService = esDemoProductService;
    }



    @RequestMapping("/getByProductName")
    public MessageResult<List<DemoProduct>> getByProductName() {
        MessageResult<List<DemoProduct>> message = new MessageResult<>();
        try {
            List<DemoProduct> products = esDemoProductService.search("", 1, 20);

            message.setData(products);
            // Thread.sleep(10*1000);
//            message = productService.getPageData(viewModel);//查看缓存问题
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }

}
