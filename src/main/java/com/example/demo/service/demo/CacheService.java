package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.utility.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/*
<!-- ehcache依赖 -->
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>2.10.6</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
————————————————
版权声明：本文为CSDN博主「Asurplus」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/qq_40065776/article/details/107560607
 */
@Service
public class CacheService {

    /*
    穿透：db没值 ，null值
    击穿：db有值 ，锁
    雪崩：大量同时过期
     */
    IProductTestService productTestService;

    @Autowired
    public CacheService(IProductTestService productTestService) {
        this.productTestService = productTestService;
    }

    public void test() {

    }

    //,unless="#result == null"  不缓存null值。默认缓存null值
    // @Cacheable(value = "productTest", key = "#id" ,unless = "#result == null")
    @Cacheable(value = "productTest", key = "#id")
    public ProductTest getProductTest(int id) {
        return productTestService.getById(id);
    }

    //缓存只删除不更新
//    @CacheEvict(value = "productTest", key = "#request.getId()")

    // @CachePut 注解要返回修改过的实体，如果没有返回值(void)redis 缓存null.
    @CachePut(value = "productTest", key = "#request.getId()")
    public ProductTest cacheServicePut(ProductTest request) {
//        ProductTest productTest = productTestService.getById(request.getId());
        productTestService.updateById(request);
        return productTestService.getById(request.getId());
    }

    //删除数据库，再删redis缓存，纵使数据库不存在也会删除redis缓存
    @CacheEvict(value = "productTest", key = "#id")
    public void cacheServiceEvict(BigInteger id) {
        productTestService.removeById(id);
    }
}
