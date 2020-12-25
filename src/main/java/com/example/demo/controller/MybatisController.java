package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.mybatissql.MybatisSqlService;
import com.example.demo.service.wms.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/mybatis")
public class MybatisController {
    /*
    注解方式:不适合动态sql,写的比较繁杂，代码可读性不太好。功能如表单查询。
    原生SQL:还是要在xml定义方法名（mapper里方法），在service里调用mapper传入sql语句，还是没有摆脱xml。
    XML方式：
     */


    @Autowired
    private MybatisSqlService mybatisSqlService;

    @RequestMapping("/getPageData")
    public MessageResult<PageData<ProductVM>> getPageData(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageData(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }
}
