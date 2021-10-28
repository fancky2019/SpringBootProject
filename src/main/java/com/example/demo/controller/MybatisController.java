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

/*
https://mybatis.org/mybatis-3/zh/sqlmap-xml.html
 */
@RestController
@RequestMapping("/mybatis")
public class MybatisController {
    /*
    注解方式:不适合动态sql,写的比较繁杂，代码可读性不太好。功能如表单查询。
    原生SQL:还是要在xml定义方法名（mapper里方法），在service里调用mapper传入sql语句，还是没有摆脱xml。
    XML方式：
     */
    /*
     f返回整形：不必指定resultType,指定了还报错
     */

    /*
    ${}变量占位符：的功能是直接进行字符串拼接,将参数替换${param}。一般用于数据库基础连接配置。
    #{}参数占位符，变量会被变异成？。
     */


    /*
         一级缓存：要开启事务，在同一事务内两次查询利用缓存。实际没多大用
          二级缓存 <select id="getPageData" useCache="true" resultType="com.example.demo.model.viewModel.ProductVM" >
          resultType 要实现Serializable接口，可序列化。

         二级缓存：
         demo  wms.ProductMapper
         Cache Hit
        mapper设置  <cache eviction="LRU" flushInterval="100000" readOnly="true" size="1024"/>
        查询语句设置  <select id="getPageData" useCache="true" resultType="com.example.demo.model.viewModel.ProductVM" >

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

    @RequestMapping("/getPageDataByHelper")
    public MessageResult<PageData<ProductVM>> getPageDataByHelper(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageDataByHelper(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }
    @RequestMapping("/getPageDataByHelperCTE")
    public MessageResult<PageData<ProductVM>> getPageDataByHelperCTE(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.getPageDataByHelperCTE(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }
    @RequestMapping("/concatSelect")
    public MessageResult<PageData<ProductVM>> concatSelect(ProductVM viewModel) {
        MessageResult<PageData<ProductVM>> message = new MessageResult<>();
        try {
            message = mybatisSqlService.concatSelect(viewModel);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        } finally {
            return message;
        }
    }


}
