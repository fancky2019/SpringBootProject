package com.example.demo.controller;

import com.example.demo.model.elasticsearch.DemoProduct;
import com.example.demo.model.elasticsearch.ShipOrderInfo;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.request.ShipOrderInfoRequest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.elasticsearch.ESDemoProductService;
import com.example.demo.service.elasticsearch.ShipOrderInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
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

 1、单个索引里字段数量上限为1000
 2、单个索引里文档数量上限为2,147,483,519
 3、查询结果数量上限默认为10000

 */
@RestController
@RequestMapping("/Elasticsearch")
public class ElasticsearchController {

//    @Autowired
//    private ESDemoProductService esDemoProductService;


    @Autowired
    private final ESDemoProductService esDemoProductService;

    @Autowired
    private ShipOrderInfoService shipOrderInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    //构造函数注入
    public ElasticsearchController(ESDemoProductService esDemoProductService) {
        this.esDemoProductService = esDemoProductService;
    }

    @RequestMapping("/getDemoProductPageBySearchAfter")
    public MessageResult<PageData<DemoProduct>> getDemoProductPageBySearchAfter(@RequestBody DemoProductRequest request) {
        MessageResult<PageData<DemoProduct>> message = new MessageResult<>();
        try {
            PageData<DemoProduct> pageData = esDemoProductService.searchAfter(request);
            message.setData(pageData);
            // Thread.sleep(10*1000);
//            message = productService.getPageData(viewModel);//查看缓存问题
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;

    }

    @RequestMapping("/getDemoProductPage")
    public MessageResult<PageData<DemoProduct>> getDemoProductPage(@RequestBody DemoProductRequest request) {
        MessageResult<PageData<DemoProduct>> message = new MessageResult<>();
        try {
            PageData<DemoProduct> pageData = esDemoProductService.search(request);
            message.setData(pageData);
            // Thread.sleep(10*1000);
//            message = productService.getPageData(viewModel);//查看缓存问题
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;

    }


    @RequestMapping("/getDemoProductPageByAlias")
    public MessageResult<PageData<DemoProduct>> getDemoProductPageByAlias(@RequestBody DemoProductRequest request) {
        MessageResult<PageData<DemoProduct>> message = new MessageResult<>();
        try {
            PageData<DemoProduct> pageData = esDemoProductService.searchByAlias(request);
            message.setData(pageData);
            // Thread.sleep(10*1000);
//            message = productService.getPageData(viewModel);//查看缓存问题
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;

    }

    @PostMapping("/createDemoProduct")
    public MessageResult<Void> createDemoProduct() throws Exception {
        esDemoProductService.createDemoProduct();
        return MessageResult.success();
    }


    //get 可以在body 内设置参数，但是通常用post 方法
    @PostMapping("/getShipOrderInfoList")
    public MessageResult<PageData<ShipOrderInfo>> getShipOrderInfoList(@RequestBody ShipOrderInfoRequest request) throws Exception {
        return MessageResult.success(shipOrderInfoService.search(request));
    }

    @GetMapping("/addBatch")
    public MessageResult<Void> addBatch() throws Exception {
        shipOrderInfoService.addBatch();
        return MessageResult.success();
    }

    @PostMapping("/deleteShipOrderInfo")
    public MessageResult<Void> deleteShipOrderInfo() throws Exception {
        shipOrderInfoService.deleteShipOrderInfo();
        return MessageResult.success();
    }


    @GetMapping("/aggregationTopBucketQuery")
    public MessageResult<Void> aggregationTopBucketQuery(ShipOrderInfoRequest request) throws Exception {
        shipOrderInfoService.aggregationTopBucketQuery(request);
        return MessageResult.success();
    }

    @GetMapping("/scriptQuery")
    public MessageResult<Void> scriptQuery() throws Exception {
        //   shipOrderInfoService.scriptQuery();
        return MessageResult.success();
    }

    @PostMapping("/aggregationStatisticsQuery")
    public MessageResult<LinkedHashMap<String, BigDecimal>> aggregationStatisticsQuery(@RequestBody ShipOrderInfoRequest request) throws Exception {
        LinkedHashMap<String, BigDecimal> map = shipOrderInfoService.aggregationStatisticsQuery(request);
        return MessageResult.success(map);
    }

    @GetMapping("/dateHistogramStatisticsQuery")
    public MessageResult<LinkedHashMap<Object, Double>> dateHistogramStatisticsQuery(ShipOrderInfoRequest request) throws Exception {
        LinkedHashMap<Object, Double> map = shipOrderInfoService.dateHistogramStatisticsQuery(request);
        return MessageResult.success(map);
    }


}
