package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.service.wms.ProductService;
import com.example.demo.utility.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
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


 @Cacheable - “查” 操作  .
 核心思想： 如果缓存中有，就直接返回；如果没有，才执行方法并放入缓存。
 工作流程：
 检查缓存中是否存在以给定参数为键的结果。
 如果存在（缓存命中）：直接返回缓存的结果，方法体不会被执行。
 如果不存在（缓存未命中）：执行方法体，将返回的结果存入缓存，然后再返回结果。

 @CachePut - “增” 或 “改” 操作
 核心思想： 无论如何，都要执行方法，并用它的返回值更新缓存。
 工作流程：
 总是会执行方法体。
 将方法返回的结果按指定的键存入缓存。

 @CacheEvict - “删” 操作
 核心思想： 从缓存中移除一个或所有数据。
 工作流程： 执行方法，然后（或在此之前）根据配置清除指定的缓存。
 allEntries：是否清空整个缓存区域。如果设为 true，会忽略 key，清除 cacheNames 下的所有缓存项。s
 beforeInvocation：清除操作是在方法执行之前还是之后进行。默认为 false（方法执行之后清除）。如果方法可能抛出异常，设为 true 可以确保即使方法失败，缓存也被清除。

 */
@Service
public class CacheService  implements ApplicationContextAware {

    @Autowired
    @Lazy  // 避免循环依赖
    private CacheService self; // 注入自身代理

    /*
    穿透：db没值 ，null值  缓存穿透是指查询一个根本不存在的数据，这个数据在缓存和数据库中都不存在。导致每次请求都会直接访问数据库，失去了缓存保护的意义。
    击穿：db有值 ，锁    缓存击穿是指一个热点key在缓存过期的瞬间，同时有大量请求访问这个key，导致所有请求都直接访问数据库。
    雪崩：大量同时过期
     */
    IProductTestService productTestService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public CacheService(IProductTestService productTestService) {
        this.productTestService = productTestService;
    }

    public void test() {

    }

//    @Cacheable(cacheNames = "hot_products", key = "#id")    hot_products::123  redis desktop 中:: 有有空节点。要自定义 RedisConfig -->redisCacheManager 生成key
//    @Cacheable(cacheNames = "hot_products", key = "'product:' + #id")  hot_products::product:123




    //注意null,要设置一段过期时间，如果bull不设置缓存造成缓存穿透查db
    //key：指定缓存项的键。默认是方法的所有参数，但可以通过 SpEL 表达式自定义。例如 key = "#id"。
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



//    多缓存区域）：
//    @Cacheable(cacheNames = "cache-permanent", key = "'product:' + #id",
//            unless = "#result == null")
//    public Product findProductById(Long id) {
//        Product product = productRepository.findById(id);
//        if (product != null) {
//            return product;
//        }
//
//        // 如果是null，可以记录到临时缓存

    // 从容器中获取代理对象
//    ProductService proxy = applicationContext.getBean(ProductService.class);
//        proxy.cacheNullValue(id);
//       self. cacheNullValue(id);
//        return null;
//    }
//
//    @Cacheable(cacheNames = "cache-temporary", key = "'product_null:' + #id")
//    public void cacheNullValue(Long id) {
//        // 这个方法只用于缓存null值的标记
//    }







}
