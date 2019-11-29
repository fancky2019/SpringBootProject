package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
具体参见workspace 的项目multipledatasource的MultipleDataSourceController
 */
@RestController
@RequestMapping("/transaction")
public class MultipleDatasourceTransactionController {
    /*
     * 分布式数据库：部署在多个服务器上。
     */

    /*
     *atomikos:支持的是同一项目的多个数据库连接的事务，也就支持了分布式数据库（多个服务器上的数据库）。
     *         但是没找到它支持分布式应用程序的多连接数据库事务，即多个应用程序内的事务。如：MicroServiceA
     *         更新了DB-A又调用MicroServeB更新DB-B。
     *
     */

    /*
     *分布式应用程序事务解决方案。
     * 一、TCC：可以做到严格的数据正确性。但是并发量不高。
     * 二、利用消息中间件实现高并发的异步事务。但是难100%做到数据正确性。单通过人工手动回滚事务可以做到正确性。
     *     此设计思路见：SpringBootProject项目的DistributedTranscationController
     */
}
