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

/*
es和关系数据库都存在跳页问题。性能差
search_after可以避免深度分页带来的性能问题，可以实时的获取下一页文档；
(上一页下一页 app 下拉获取、游标只读向前)

不支持指定页数(不支持跳页)，只能向下翻；
需要加入排序 sort,并且排序的字段一定要是唯一的（一般id）。；
避免获取过多数据：业务层控制、添加过滤条件

es默认分页：from size,默认值分别为：0,10。 max_result_window 默认值是1W。每个分片会把from+size的数据
           返回给协调节点，供最终选择排序，会加大网络、协调节点内存、cpu、oom风险
游标查询 Scroll：游标有过期时间，

search_after：实时查询
scroll：非实时

scroll api 查询
基于快照查询，此时如果有新数据插入索引，该查询不会查到
其中scrollRequest.scroll("1m");中设置的是scroll请求的上下文的存活时间

不再建议使用scroll API进行深度分页。如果要分页检索超过 Top 10,000+ 结果时，推荐使用：PIT + search_after。
 */
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
