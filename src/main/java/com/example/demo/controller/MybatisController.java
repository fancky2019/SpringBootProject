package com.example.demo.controller;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.PageData;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.service.demo.DemoProductService;
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

    @Autowired
    private DemoProductService demoProductService;

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

    /*
   在不设置时，也就是默认情况是任何方式的请求都可以(不管什么get/post/.....)
在文件上传的时候只能用post（因为其post请求（数据量可以很大）是在请求体里面，而get请求（且数据量有限）是在路径后面进行拼接）
其中RequestMapping在设置Method属性后意义为不仅要满足其value属性还要满足method属性了（RequestMethod.GET）

     */
    @RequestMapping(value = "/concatSelect")
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

    @RequestMapping(value = "/test")
    public MessageResult<PageData<Void>> test() {
        MessageResult<PageData<Void>> message = new MessageResult<>();
        try {
            demoProductService.insertTransactional();
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;
    }

    /**
     * 更新覆盖：两个事务对数据库写，要加悲观锁（redisson，for update 容易死锁）使得事务串化。更新的时候加上版本号条件
     *         如果返回受影响行为0，更新失败。抛异常，否则继续执行。
     * @return
     */
    @RequestMapping(value = "/coverUpdate")
    public MessageResult<Void> coverUpdate() {
        MessageResult<Void> message = new MessageResult<>();

        try {
            demoProductService.insertTransactional();
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;
    }



}
