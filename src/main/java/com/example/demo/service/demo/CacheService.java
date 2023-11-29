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

spring-boot-starter-cache + Ehcache或
spring-boot-starter-cache + caffeine使用 通过注解的方式

也可以单独引用caffeine使用 ，put  get
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
