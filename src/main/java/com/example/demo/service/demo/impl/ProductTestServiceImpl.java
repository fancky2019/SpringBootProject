package com.example.demo.service.demo.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.aop.aspect.executeorder.PointcutExecuteOrderOne;
import com.example.demo.aop.aspect.executeorder.PointcutExecuteOrderTwo;
import com.example.demo.config.FtpConfig;
import com.example.demo.dao.demo.PersonMapper;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.easyexcel.DropDownSetField;
import com.example.demo.easyexcel.ExcelStyleConfig;
import com.example.demo.easyexcel.GXDetailListVO;
import com.example.demo.easyexcel.ResoveDropAnnotationUtil;
import com.example.demo.easyexcel.handler.DropDownCellWriteHandler;
import com.example.demo.listener.eventbus.CustomEvent;
import com.example.demo.listener.eventbus.MyCustomEvent;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.Person;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.request.TestRequest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.service.demo.IMqMessageService;
import com.example.demo.service.demo.IProductTestService;
import com.example.demo.service.ftp.FtpService;
import com.example.demo.service.wms.ProductService;
import com.example.demo.utility.ConfigConst;
import com.example.demo.utility.ExcelUtils;
import com.example.demo.utility.MqSendUtil;
import com.example.demo.utility.RedisKeyConfigConst;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * mybatisPlus批量
 * 服务实现类
 *
 *
 * @author author
 * @since 2022-11-17
 */
@Service
@Slf4j
public class ProductTestServiceImpl extends ServiceImpl<ProductTestMapper, ProductTest> implements IProductTestService {


    /**
     *
     默认jdk 动态代理：
     Advisor 不全
     selfProxy 触发了 early reference  先创建了一个“半成品代理”
     不是 @Transactional 失效，而是你拿到的代理对象根本没有事务拦截器。
     @Async + 自注入 + 提前代理暴露。
     自注入selfProxy导致Spring在Bean创建过程中提前创建了一个不完整的代理（0 advisors）。

     改成cglib 动态代理：从applicationContext获取bean可获取全部Advisor信息。

     cglib配置：
     //@EnableAspectJAutoProxy(proxyTargetClass = true)  // 1. AspectJ 代理.启动类已配置
     @EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE,proxyTargetClass = true)  // 事务最低优先级
     @EnableAsync(order = Ordered.HIGHEST_PRECEDENCE,proxyTargetClass = true)  // 异步最高优先级
     //@Primary // 指定为默认实现
     public class ThreadPoolExecutorConfig {
     */
    @Autowired
    @Lazy  // 防止循环依赖
    private IProductTestService selfProxy;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ProductTestMapper productTestMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductService productService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @Autowired
    private IMqMessageService mqMessageService;


    @Autowired
    private MqSendUtil mqSendUtil;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private BusProperties busProperties;
    @Autowired
    private FtpService ftpService;

    @Autowired
    private FtpConfig ftpConfig;

    public ProductTestServiceImpl(ProductTestMapper productTestMapper) {
        this.productTestMapper = productTestMapper;
    }


    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void mybatisPlusTest() throws InterruptedException {
        Object object = selfProxy;
        // 从 ApplicationContext 获取代理对象
        IProductTestService proxyService = applicationContext.getBean(IProductTestService.class);
        //AopContext.currentProxy() 返回的是当前方法调用链中的代理对象,返回的是 ScheduledTasks 的代理
        Object proxyObj = AopContext.currentProxy();
        IProductTestService productTestService = null;
        if (proxyObj instanceof IProductTestService) {
            productTestService = (IProductTestService) proxyObj;

        }
//        this.baseMapper.deleteBatchIds();
//        this.saveEntity();
//        saveOrUpdateBatch();
//        updateBatchTest();
//        queryById();
//        queryTest();
//        updateTest();
//        productTestService.saveBatchTest();
//        productTestService.updateBatchByIdTest();
        productTestService.deleteBatchTest();
//        page();
//        queryParam();
//        truncateTest();
//        deleteTableDataTest();
//        selectMaxId();
//        updateField();
//        updateEntity();
        /*
        mybatis  缓存默认 一级缓存 sqlSession 级别 ，二级缓存 mapper 级别
        spring mybatis 缓存需要在事务内开启，否则无效。

        下面两次查询如果开启事务只会执行一次数据库查询，不开启事务会执行两次数据库查询
         */
//        ProductTest productTest = this.getById(17);
//
//        Thread.sleep(5000);
//        ProductTest productTest1 = this.getById(17);
    }

    private void saveEntity() {
        ProductTest productTest = new ProductTest();
        productTest.setGuid(UUID.randomUUID().toString());
        productTest.setProductName("productName");
        productTest.setProductStyle("productStyle");
        productTest.setImagePath("D:\\fancky\\git\\Doc");
        productTest.setCreateTime(LocalDateTime.now());
        productTest.setModifyTime(LocalDateTime.now());
        productTest.setStatus(1);
        productTest.setDescription("setDescription_sdsdddddddddddddddd");
        productTest.setTimestamp(LocalDateTime.now());
        this.save(productTest);

    }

    //region  mybatisPlus批量  updateBatchById  saveBatch saveOrUpdateBatch


    /**
     * 默认情况下，saveBatch() 的底层实现是：
     * 不是真正的批量 INSERT 语句
     * 实际上会生成多条单独的 INSERT 语句：
     * INSERT INTO product_test (name, price) VALUES ('产品1', 100);
     * INSERT INTO product_test (name, price) VALUES ('产品2', 200);
     * INSERT INTO product_test (name, price) VALUES ('产品3', 300);
     *
     * 数据量大自定义xml
     * INSERT INTO product_test (name, price) VALUES ().(),
     *
     *
     *  SqlSession 的关系
     * 默认行为：
     * saveBatch 内部会获取一个 SqlSession
     * 设置执行器类型为 BATCH
     * 执行批量插入
     *将多条插入语句合并执行，提高插入效率
     * 内部使用了 SqlSession#flushStatements() 来执行批量操作
     * 最后提交事务并关闭 session
     *
     *
     * 100条 cost 70ms
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchTest() {
        List<ProductTest> productTestList = new ArrayList<ProductTest>();
        ProductTest productTest = null;
        for (int i = 0; i < 100; i++) {
            productTest = new ProductTest();
            productTest.setGuid(UUID.randomUUID().toString());
            productTest.setProductName("productName");
            productTest.setProductStyle("productStyle");
            productTest.setImagePath("D:\\fancky\\git\\Doc");
            productTest.setCreateTime(LocalDateTime.now());
            productTest.setModifyTime(LocalDateTime.now());
            productTest.setStatus(1);
            productTest.setDescription("setDescription_sdsdddddddddddddddd");
            productTest.setTimestamp(LocalDateTime.now());
            productTestList.add(productTest);
        }
        this.saveBatch(productTestList);
    }

    /**
     * 获取批处理模式的 SqlSession
     * 循环处理每一条数据：
     * 为每条数据生成更新SQL
     * 添加到批处理中
     * 达到批量大小时执行 flushStatements()
     * 提交事务
     * 关闭 SqlSession
     *
     *
     *
     *
     * 耗时108ms
     * 会更新所有字段  ,更新部分字段可以大大减少耗时
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchByIdTest() {
        List<BigInteger> ids = new ArrayList<>();

        for (int i = 1; i < 1000; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        List<ProductTest> list = this.listByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            ProductTest productTest = list.get(i);
            productTest.setProductName("22batchUpdate" + i);
        }


        StopWatch stopWatch = new StopWatch("saveOrUpdateBatchTest");
        stopWatch.start("BatchInsert_Trace1");

//        因为mybatis返回的默认是匹配的行数，而不是受影响的行数，如何设置返回的是受影响的行数，useAffectedRows=true
        //mysql 连接字段穿添加  &useAffectedRows=true 返回0 ，不加返回1。
        //for循环多个update 语句以分号结束，update 执行会返回1.因为执行update id 就一条
        //批量更新还是要加锁，避免并发访问，或者for 循环每条更新判断
        //批量更新、批量条件更新。加锁，避免并发访问，或者for 循环每条更新判断。使用sqlSession 一次提交
        /*
        UPDATE demo_product  SET guid='a02a0ed4-c685-4d9a-949e-bcba60f17c97',
product_name='22batchUpdate702',
product_style='productStyle1200002',
image_path='D:\\fancky\\git\\Doc',
create_time='2023-11-03 22:29:11.394',
modify_time='2023-11-03 22:29:11.394',
status=1,
description='setDescription_sdsdddddddddddddddd',
timestamp='2023-11-03 22:29:11.0'  WHERE id=703


SELECT id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp FROM demo_product WHERE id=703
         */
        //先 select,判断有没有，然后执行insert ot update
//        boolean result1 = this.saveOrUpdateBatch(list);

//        this.saveBatch()
        boolean result = this.updateBatchById(list);

        stopWatch.stop();
        //        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

    }

    /**
     * 执行脚本
     * com.example.demo.dao.demo.ProductTestMapper.deleteByIds - ==>  Preparing: DELETE FROM demo_product WHERE id IN ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchTest() {

        List<BigInteger> ids = new ArrayList<>();

        for (int i = 125; i < 226; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        boolean result = this.removeBatchByIds(ids);
//        this.listByIds()
    }

    //endregion


    private void updateBatchTest() {

        List<BigInteger> ids = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        List<ProductTest> list = this.listByIds(ids);
        //mybatis 只找到根据id 批量更品，如果批量条件更新还是要写 xml for 循环
        //updateBatchById 内部还是一条一条update 语句，只不过一次提交batchSize默认1000次
        //jdbc 的batch  操作update 和insert 都是   preparedStatement.addBatch();  preparedStatement.executeBatch();
        this.updateBatchById(list);
    }

    public void saveOrUpdateBatchTest1() {
        List<ProductTest> productTests = new ArrayList<ProductTest>();
        ProductTest productTest = new ProductTest();
        productTest.setId(new BigInteger("100001"));
        productTest.setGuid("dssddssd");
        productTests.add(productTest);

        productTest = new ProductTest();
        productTest.setId(new BigInteger("100002"));
        productTest.setGuid("sdqwe43");
        productTests.add(productTest);

//        this.saveOrUpdateBatch(productTests);
        //更新
        this.updateBatchById(productTests);
        //保存
//        this.saveBatch(productTests);
        this.save(productTest);
        this.removeById(productTest);
        this.updateById(productTest);
//        this.listByIds();
        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
//        queryWrapper.select("","");
//        queryWrapper.eq("",1);
//        queryWrapper.ne();
        this.list(queryWrapper);
    }


    private void queryParam() {
        ProductTest productTest = new ProductTest();
        LambdaQueryWrapper<ProductTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductTest::getProductName, "productName_xiugai55555");
        //不为空查询
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(productTest.getProductName()), ProductTest::getProductName, productTest.getProductName());
        lambdaQueryWrapper.last("limit 3");
        //分页
        List<ProductTest> list1 = this.list(lambdaQueryWrapper);


        int m = 0;
    }

    private void queryById() {
        ProductTest list1 = this.getById(17);
        int m = 0;
    }


    private void page() {
        /**
         * 未配置拦截器 内存分页
         *
         * SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product
         *
         *  WHERE (id = 1)
         *
         *  配置mybatis-pls 拦截器之后
         *   SELECT COUNT(*) AS total FROM demo_product WHERE (id = 1)
         *  SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product
         *
         *  WHERE (id = 1) LIMIT 10
         */
        //current :pageIndex  ,size:pageSize
        Page<ProductTest> page = new Page<>(1, 10);
        LambdaQueryWrapper<ProductTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTest::getId, 1L);
//        page 内部调用 this.getBaseMapper().selectPage
        IPage<ProductTest> productTestPage = this.page(page, wrapper);
        List<ProductTest> productTestList = productTestPage.getRecords();
        long total = productTestPage.getTotal();



        /*
        需要配置 MybatisPlusPageInterceptor 拦截器，否则是查询所有

    SELECT COUNT(*) AS total FROM demo_product WHERE (id = 1 AND product_name = 'update1')
SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product

 WHERE (id = 1 AND product_name = 'update1') LIMIT 10
         */
        wrapper.eq(ProductTest::getProductName, "update1");
        IPage<ProductTest> pageData = this.getBaseMapper().selectPage(page, wrapper);

        int m = 0;
    }

    private void queryTest() {
        /*
        原符号       <       <=      >       >=      <>
        对应函数    lt()     le()    gt()    ge()    ne()
        Mybatis-plus写法：  queryWrapper.ge("create_time", localDateTime);
        Mybatis写法：       where create_time >= #{localDateTime}


        //不加last(“desc”)默认就是升序，加上是降序方式

        List<Employee> list=empployeeMapper.selecList(
        new EntityWrapper<Employee> ()
        .eq("gender",0)
        .orderBy("age")
        .last("desc")
        );
         */

//        QueryWrapper 使用字符串来表示数据库表中的列名，
//        而 LambdaQueryWrapper 使用 Java 8 的 Lambda 表达式来更直接地指定字段名。

        /*
        getGuid的方法引用，实际传的类是SFunction 会识别到是哪个数据库字段  方法名-->字段名
       lambda的方法引用在序列化的时候，会序列化为SerializedLambda类，。

       SerializedLambda 类
         private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;


    //序列化Lambda   方法名-->字段名
    SerializedLambda serializedLambda =LambdaUtils.resolve(Son::getSons);
    String name = serializedLambda.getImplMethodName();
    String fieldName = PropertyNamer.methodToProperty(name);

         */


        LambdaQueryWrapper<ProductTest> squeryWrapper = new LambdaQueryWrapper<ProductTest>();
        squeryWrapper.select(ProductTest::getGuid, ProductTest::getProductName);
        squeryWrapper.eq(ProductTest::getStatus, 1);
//        squeryWrapper.last("limit " + startIndex + ", " + pageSize);
        squeryWrapper.last("limit 0,10");
//        queryWrapper.ne();
        String productName1 = "";
        //有条件拼接条件
        squeryWrapper.eq(StringUtils.isNotEmpty(productName1), ProductTest::getProductName, productName1);
        List<ProductTest> list11 = this.list(squeryWrapper);

        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
        queryWrapper.select("guid", "product_name");
        queryWrapper.eq("", 1);
//        queryWrapper.ne();
        String productName = "";
        //有条件拼接条件
        queryWrapper.eq(StringUtils.isNotEmpty(productName), "product_name", productName);
//        queryWrapper.orderByDesc("desc");
        List<ProductTest> list = this.list(queryWrapper);

        LambdaQueryWrapper<ProductTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductTest::getProductName, "productName_xiugai55555");
        lambdaQueryWrapper.last("limit 3");
        List<ProductTest> list1 = this.list(lambdaQueryWrapper);


        ProductTestMapper productTestMapper = this.getBaseMapper();
        //链式查询方式
        List<ProductTest> list2 = new LambdaQueryChainWrapper<ProductTest>(this.getBaseMapper()).eq(ProductTest::getProductName, "productName_xiugai55555").list();


        int m = 0;
    }

    /**
     * 一定要在 LambdaUpdateWrapper 指定更新条件否则全表更新
     */

    private void updateTest() {

        //条件更新
//        因为mybatis返回的默认是匹配的行数，而不是受影响的行数，如何设置返回的是受影响的行数，useAffectedRows=true
        //mysql 连接字段穿添加  &useAffectedRows=true 返回0 ，不加返回1。
        //for循环多个update 语句以分号结束，update 执行会返回1.因为执行update id 就一条
        //批量更新还是要加锁，避免并发访问


//        Mapper 的 update 方法的返回值是一个 int 类型，表示受影响的行数。具体来说：
//        如果返回值大于 0，表示更新成功，且返回值是实际更新的记录数。
//        如果返回值等于 0，表示没有记录被更新（可能是没有匹配的条件）。
//        如果返回值小于 0，通常表示更新失败（可能是 SQL 执行错误）。

        ProductTest productTest9 = this.getById(9);
        LambdaUpdateWrapper<ProductTest> updateWrapper22 = new LambdaUpdateWrapper<>();
        //更新为空
        updateWrapper22.set(ProductTest::getProductStyle, productTest9.getProductStyle());
        updateWrapper22.set(ProductTest::getProductName, productTest9.getProductName());
        updateWrapper22.eq(ProductTest::getId, 9);
//      不指定实体，  MetaObjectHandlerImp不会执行打印fill审计信息
        //数据没有做修改，影响的行数是0，不然返回1
        int affectRows = baseMapper.update(null, updateWrapper22);


        /*
         * UPDATE demo_product  SET product_name='update'
         *  WHERE (id = 1 AND status = 1)
         */
        LambdaUpdateWrapper<ProductTest> updateWrapper = new LambdaUpdateWrapper<>();
        //更新为空
        updateWrapper.set(ProductTest::getProductStyle, null);
        updateWrapper.set(ProductTest::getProductName, "update1");
        updateWrapper.eq(ProductTest::getId, 1);
        updateWrapper.eq(ProductTest::getStatus, 1);
        //      不指定实体，  MetaObjectHandlerImp不会执行打印fill审计信息
        //如果值不改变，update 返回false.其内部根据mapper 执行的结果判断： result >= 1
        //内部调用 update(null,updateWrapperOne);
        boolean re = this.update(updateWrapper);
//        this.update(productTest,updateWrapper);
        //  实体要指定null ，默认null，不然默认更新非空字段
//        baseMapper.update(null, updateWrapper);
        /*
      UPDATE demo_product  SET guid='380de07a-58e4-4bf6-88d8-97ed9d6b8275',
        product_name='产品名称1200001',
        product_style='productStyle1200001',
        image_path='D:\\fancky\\git\\Doc',
        create_time='2023-11-05 22:29:11.394',
        modify_time='2023-11-05 22:29:11.394',
        status=1,
        description='setDescription_sdsdddddddddddddddd',
        timestamp='2023-11-05 22:29:11.0',
        -- 先更新为实体对象的值 ，然后拼接 updateWrapper2
        product_name='update2'

         WHERE (id = 2 AND status = 2)

         */
        ProductTest productTest = this.getById(2);
        LambdaUpdateWrapper<ProductTest> updateWrapper2 = new LambdaUpdateWrapper<>();
        //设置空
        updateWrapper2.set(ProductTest::getProductName, null);
        updateWrapper2.eq(ProductTest::getId, 2);
        updateWrapper2.eq(ProductTest::getStatus, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        //如果值不改变，update 返回false.其内部根据mapper 执行的结果判断： result >= 1
        //内部调用 update(null,updateWrapperOne);
        boolean re1 = this.update(productTest, updateWrapper2);

        int m = 0;
        /*  全表更新，没有where 条件。
        UPDATE demo_product  SET guid='a02a0ed4-c685-4d9a-949e-bcba60f17c97',
            product_name='产品名称1200002',
            product_style='productStyle1200002',
            image_path='D:\\fancky\\git\\Doc',
            create_time='2023-11-05 22:29:11.394',
            modify_time='2023-11-05 22:29:11.394',
            status=1,
            description='setDescription_sdsdddddddddddddddd',
            timestamp='2023-11-05 22:29:11.0',

            product_name='update3'
         */
//        ProductTest productTest3= this.getById(3);
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getProductName, "update3");
//        boolean re3 = this.update(productTest3,updateWrapper3);


        ProductTest productTest3 = this.getById(3);
        //修改了实体的值，如果指定实体更新，会在update 语句内。
        productTest3.setProductName("");
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(StringUtils.isNotEmpty(productTest3.getProductName()), ProductTest::getProductName, "ProductName3");
        updateWrapper3.set(ProductTest::getProductStyle, "getProductStyle111");
        updateWrapper3.eq(ProductTest::getId, productTest3.getId());
        updateWrapper3.eq(ProductTest::getStatus, productTest3.getStatus());
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        //如果值不改变，update 返回false.其内部根据mapper 执行的结果判断： result >= 1
        //内部调用 update(null,updateWrapperOne);
        boolean re3 = this.update(updateWrapper3);


        ProductTest productTestOne = this.getById(1);
        if (productTestOne.getId().compareTo(BigInteger.valueOf(2)) == 0) {
//            productTest.setId(BigInteger.valueOf(0));
        }
        LambdaUpdateWrapper<ProductTest> updateWrapperOne = new LambdaUpdateWrapper<>();
        updateWrapperOne.set(ProductTest::getProductName, productTest.getProductName() + 1);
        updateWrapperOne.eq(ProductTest::getId, productTest.getId());
        //如果值不改变，update 返回false.其内部根据mapper 执行的结果判断： result >= 1
        //内部调用 update(null,updateWrapperOne);
        boolean reOne = this.update(null, updateWrapperOne);
    }

    /**
     * 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。
     * @param productName
     */
    @Transactional(rollbackFor = Exception.class)
    public void coverUpdateTestOne(String productName) throws InterruptedException {
        coverUpdateTest(productName);
        Thread.sleep(30 * 1000);
    }

    /**
     * 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。for update
     * @param productName
     */
    @Transactional(rollbackFor = Exception.class)
    public void coverUpdateTestTwo(String productName) {
        //coverUpdateTest 里等到coverUpdateTestOne 执行完才执行 update 方法
        // 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。for update
        coverUpdateTest(productName);
    }

    /**
     * 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。for update
     * @param productName
     */
    @Transactional(rollbackFor = Exception.class)
    public void coverUpdateTest(String productName) {

        //因为mybatis返回的默认是匹配的行数，而不是受影响的行数，如何设置返回的是受影响的行数，useAffectedRows=true
        //mysql 连接字段穿添加  &useAffectedRows=true 返回0 ，不加返回1。
        //for循环多个update 语句以分号结束，update 执行会返回1.因为执行update id 就一条
        //批量更新还是要加锁，避免并发访问


        ProductTest productTest3 = this.getById(new BigInteger("3"));
        Integer version = productTest3.getVersion();
        int oldVersion = version;
//        productTest3.setVersion(++version);
        //修改了实体的值，如果指定实体更新，会在update 语句内。
        productTest3.setProductName(productName);
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(StringUtils.isNotEmpty(productTest3.getProductName()), ProductTest::getProductName, productTest3.getProductName());
        updateWrapper3.set(ProductTest::getVersion, productTest3.getVersion());
        updateWrapper3.eq(ProductTest::getId, productTest3.getId());
        updateWrapper3.eq(ProductTest::getVersion, oldVersion);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        //返回更新成功失败
        // 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。
        boolean re3 = this.update(updateWrapper3);
//        在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。
        //成功返回1，失败返回0
//        int re = this.getBaseMapper().updateByPrimaryKeySelective(productTest3);


        int n = 0;
    }



    /*
    1.truncate先使用create命令创建表，然后drop源表，最后rename新表。
    2 drop只是删除元数据，所以比delete快很多，特别是大表
    3 truncate本质是ddl，需要ddl权限。ddl本身是自提交的，所以truncate也不能rollback回滚
    4 因为是truncate是重建表，所以truncate是可以整理表碎片的（delete不可以）
    5 truncate在执行有外键约束的reference表时会失败

     */

    /**
     * truncate 截断表是不会回滚的
     * truncate 会删除所有数据，并且不记录日志，不可以恢复数据，相当于保留了表结构
     */
    @Transactional(rollbackFor = Exception.class)
    public void truncateTest() {
        this.productTestMapper.truncateTest();
        int m = Integer.parseInt("m");
    }

    private void deleteTableDataTest() {
        //DELETE FROM product_test
        LambdaQueryWrapper<ProductTest> queryWrapper = new LambdaQueryWrapper<>();
        this.remove(queryWrapper);
        int m = 0;
    }

    private void selectMaxId() {
        //注：不是LambdaQueryWrapper
        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("max(id) id");
        ProductTest productTest = this.getOne(queryWrapper);
        //查不到为null
        if (productTest != null) {
            BigInteger maxId = productTest.getId();
        }
        int m = 0;
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateEntity() {
        String longStr = "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"
                + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT";
        ProductTest productTest = this.getById(2);
        LambdaUpdateWrapper<ProductTest> updateWrapper2 = new LambdaUpdateWrapper<>();
        //设置空
        updateWrapper2.set(ProductTest::getProductName, null);
        updateWrapper2.set(ProductTest::getProductStyle, longStr);
        updateWrapper2.eq(ProductTest::getId, 2);
        updateWrapper2.eq(ProductTest::getStatus, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        //限执行MetaObjectHandler，然后执行事务成功回调
//        boolean re1 = this.update(productTest, updateWrapper2);


        productTest.setProductStyle(longStr);
        List list = new ArrayList();
        list.add(productTest);
        this.updateBatchById(list);
        mqSendUtil.send(null);
        int m = 0;
    }

    /**
     * 更新表的指定字段
     * 批量更新指定字段
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateField() throws InterruptedException {


        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getProductStyle, "1");
        updateWrapper3.eq(ProductTest::getId, 1);

        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);
        Thread.sleep(90 * 1000);
//        UpdateWrapper<ProductTest> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("ID", 1);//条件
//        updateWrapper.set("product_Name", "abc");//要更新的列
//        //  实体要指定null ，不然默认更新非空字段
//        int ref = baseMapper.update(null, updateWrapper);

        int m = 0;
        //根据条件删除
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        wrapper.eq("user","zyh3"); //通过wrapper设置条件
//        baseMapper.delete(wrapper);
//        service.remove(wrapper);


//MyBatis-Plus updateById方法更新不了空字符串/null解决方法
//        userService.update(null,new UpdateWrapper<User>().lambda()
//                .set(User::getUserName,null)
//                .eq(User::getUserId,user.getUserId()));


//        批量更新指定字段
//                <update id="batchUpdateItems">
//                <foreach collection="list" item="item" separator=";">
//                UPDATE apply_receipt_order_item
//        SET field1 = #{item.field1},
//        field2 = #{item.field2}
//        WHERE id = #{item.id}
//    </foreach>
//                </update>


    }

    //region redis

    /**
     * * 雪崩：随机过期时间
     * * 击穿：分布式锁（表名），没有取到锁，sleep(50)+重试 .获取不到锁，抛异常处理 服务器繁忙，稍后重试
     * * 穿透：分布式锁（表名）+设置一段时间的null值，没有取到锁，sleep(50)+重试
     *
     * @param id
     * @return
     * @throws Exception
     */
    public String getStringKey(@PathVariable int id) throws Exception {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = ConfigConst.DEMO_PRODUCT_PREFIX + id;
        String val = valueOperations.get(key);
        if (StringUtils.isEmpty(val)) {

            String lockKey = ConfigConst.DEMO_PRODUCT_PREFIX + "redisson";
            //获取分布式锁，此处单体应用可用 synchronized，分布式就用redisson 锁
            RLock lock = redissonClient.getLock(lockKey);
            try {

                boolean lockSuccessfully = lock.tryLock(30, 60, TimeUnit.SECONDS);
                if (!lockSuccessfully) {
                    log.info("Thread - {} 获得锁 {}失败！锁被占用！", Thread.currentThread().getId(), lockKey);

                    //获取不到锁，抛异常处理 服务器繁忙，稍后重试
//                    throw new Exception("服务器繁忙，稍后重试");
                    return null;
                }
                BigInteger idB = BigInteger.valueOf(id);
                ProductTest productTest = this.getById(idB);
                //穿透：设置个空值
                if (productTest == null) {
                    valueOperations.set(key, ConfigConst.EMPTY_VALUE);
                    redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                } else {
                    String json = objectMapper.writeValueAsString(productTest);
                    //要设置个过期时间
                    valueOperations.set(key, json);
                    //[100,2000)
                    long expireTime = ThreadLocalRandom.current().nextInt(3600, 24 * 3600);
                    redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                throw e;
            } finally {
                //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
                //unlock 删除key
                //如果锁因超时（leaseTime）会抛异常
                lock.unlock();
            }


        } else {
            if (ConfigConst.EMPTY_VALUE.equals(val)) {
                return null;
            }
        }


        return val;
    }
    //endregion

    //region easyexcel 导出  分页导出

    /**
     *
     *-- 性能最好
     * select * from demo_product
     *         where 1=1
     * 				and id >1810000
     *         limit 5000
     *
     * -- 性能其次
     * select  d.* from (
     * select id from demo_product
     *         where 1=1
     * 				and id >1710000
     *         limit 5000
     * 				) t
     * join demo_product d on t.id=d.id
     *
     *
     * -- 性能最差
     * select * from demo_product  where 1=1 limit 2180000  ,5000
     *
     * 250 W不到3分钟
     */
    public void exportByPage(HttpServletResponse response, DemoProductRequest request) throws IOException, NoSuchFieldException, IllegalAccessException {

        String fileName = "DemoProduct_" + System.currentTimeMillis();
        prepareResponds(fileName, response);
        // 这里 需要指定写用哪个class去写
        int stepCount = 10000;


//        request.setPageIndex(1);
//        request.setPageSize(stepCount);
//        List<ProductTest> list = getPageData(request);
//        EasyExcel.write(response.getOutputStream(), ProductTest.class).sheet("表名称").doWrite(list);


        //细化设置
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取改类声明的所有字段
        Field[] fields = ProductTest.class.getDeclaredFields();

        // 响应字段对应的下拉集合
        Map<Integer, String[]> map = new HashMap<>();
        Field field = null;
        List<Field> fieldList = new ArrayList<>();
        // 过滤掉ExcelIgnore的列
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            int modifiers = field.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                // 解析注解信息
                ExcelIgnore excelIgnore = field.getAnnotation(ExcelIgnore.class);
                if (null == excelIgnore) {
                    fieldList.add(field);
                }
            }

        }

        // 循环判断哪些字段有下拉数据集，并获取
        for (int i = 0; i < fieldList.size(); i++) {
            field = fieldList.get(i);
            // 解析注解信息
            DropDownSetField dropDownSetField = field.getAnnotation(DropDownSetField.class);
            if (null != dropDownSetField) {
                String[] sources = ResoveDropAnnotationUtil.resove(dropDownSetField);
                if (null != sources && sources.length > 0) {
                    map.put(i, sources);
                }
            }
        }


        //多个sheet页写入
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.autoCloseStream(true);
        //实体的表头
//        builder.head(ProductTest.class);
//        builder.head()

//        //动态列
//        if (CollectionUtils.isNotEmpty(request.getExportFieldList())) {
//            builder.includeColumnFieldNames(request.getExportFieldList());
//            //        builder.excludeColumnFieldNames()
//        }

        //动态列和列名
        LinkedList<String> includeColumnFieldList = new LinkedList();
        LinkedList<List<String>> includeColumnPropertyList = new LinkedList();
        if (request.getFieldMap() != null && request.getFieldMap().size() > 0) {
            for (String key : request.getFieldMap().keySet()) {
                includeColumnFieldList.add(key);
                List<String> list = new ArrayList<>();
                list.add(request.getFieldMap().get(key));
                includeColumnPropertyList.add(list);

            }
            builder.includeColumnFieldNames(includeColumnFieldList);
            //序号和实体对象导出的序号不同
//            builder.head(includeColumnPropertyList);
            builder.head(ExcelUtils.getClassNew(new ProductTest(), request.getFieldMap()));

        } else {
            builder.head(ProductTest.class);
        }

//        builder.registerWriteHandler(new ExcelStyleConfig(Lists.newArrayList(7), null, null));
////        if (flag == 0 || flag == 2) {
//        builder.registerWriteHandler(new ExcelStyleConfig(Lists.newArrayList(20), null, null));
//        builder.head(ProductTest.class);
////        } else {
////            builder.registerWriteHandler(new ExcelStyleConfig(null,null,null));
////            builder.head(GXDetailListLogVO.class);
////        }

        ArrayList<String> includeColumnFieldNames = new ArrayList<>();
        //下拉框
        builder.registerWriteHandler(new DropDownCellWriteHandler(map));
//        builder.includeColumnFieldNames(includeColumnFieldNames)
        builder.file(outputStream);

        //不能重命名，重命名就没有XLSX格式后缀
        builder.excelType(ExcelTypeEnum.XLSX);
        ExcelWriter writer = builder.build();


        long count = this.baseMapper.selectCount(Wrappers.emptyWrapper());
//        count = 999;
        long loopCount = count / stepCount;
        long remainder = count % stepCount;
        if (remainder > 1) {
            loopCount++;
        }

        long sheetSize = 1000000;
        long sheetLoopCount = count / sheetSize;
        long sheetRemainder = count % sheetSize;
        if (sheetRemainder > 1) {
            sheetRemainder++;
        }
        int sheetIndex = 0;
        int maxId = 0;
        WriteSheet sheet = EasyExcel.writerSheet(0, "DemoProduct" + sheetIndex).build();
        for (int i = 1; i <= loopCount; i++) {
            request.setMaxId(maxId);
            request.setPageIndex(i);//es 要-1
            request.setPageSize(stepCount);
            //getPage 会执行获取count脚本
//            List<ProductTest> list = getPageData(request);
            //超过200W 查询要5s
//            List<ProductTest> list =  this.productTestMapper.getPageData(request);
            //采用最大ID，可0.5s查询到结果
            List<ProductTest> list = this.productTestMapper.getPageDataOptimization(request);
            int total = i * stepCount;
            writer.write(list, sheet);
            if (total % sheetSize == 0) {
                sheetIndex += 1;
                sheet = EasyExcel.writerSheet(sheetIndex, "DemoProduct" + sheetIndex).build();
//                WriteSheet writeSheet = EasyExcel.writerSheet(i, "模板" + i).build();
            }
            maxId = list.stream().map(p -> p.getId().intValue()).max(Comparator.comparing(Integer::intValue)).orElse(0);

        }


        writer.finish();


    }

    @Override
    public void exportDemoProductTemplate(HttpServletResponse response) throws IOException {
        String fileName = "DemoProduct_" + System.currentTimeMillis();
        prepareResponds(fileName, response);
        // EasyExcel.write(response.getOutputStream(), ProductTest.class).sheet("表名称").doWrite(new ArrayList<ProductTest>());

        //细化设置
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取改类声明的所有字段
        Field[] fields = ProductTest.class.getDeclaredFields();
        // 响应字段对应的下拉集合
        Map<Integer, String[]> map = new HashMap<>();
        Field field = null;
        List<Field> fieldList = new ArrayList<>();
        // 过滤掉ExcelIgnore的列
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            int modifiers = field.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                // 解析注解信息
                ExcelIgnore excelIgnore = field.getAnnotation(ExcelIgnore.class);
                if (null == excelIgnore) {
                    fieldList.add(field);
                }
            }

        }

        // 循环判断哪些字段有下拉数据集，并获取
        for (int i = 0; i < fieldList.size(); i++) {
            field = fieldList.get(i);
            // 解析注解信息
            DropDownSetField dropDownSetField = field.getAnnotation(DropDownSetField.class);
            if (null != dropDownSetField) {
                String[] sources = ResoveDropAnnotationUtil.resove(dropDownSetField);
                if (null != sources && sources.length > 0) {
                    map.put(i, sources);
                }
            }
        }


        //多个sheet页写入
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.autoCloseStream(true);
//        builder.head()
        builder.head(ProductTest.class);


//        builder.excludeColumnFieldNames()

////        if (flag == 0 || flag == 2) {
//        builder.registerWriteHandler(new ExcelStyleConfig(Lists.newArrayList(20), null, null));
//        builder.head(ProductTest.class);
////        } else {
////            builder.registerWriteHandler(new ExcelStyleConfig(null,null,null));
////            builder.head(GXDetailListLogVO.class);
////        }
        //下拉框
        builder.registerWriteHandler(new DropDownCellWriteHandler(map));
        builder.file(outputStream);

        //不能重命名，重命名就没有XLSX格式后缀
        builder.excelType(ExcelTypeEnum.XLSX);
        ExcelWriter writer = builder.build();
        WriteSheet sheet = EasyExcel.writerSheet(0, "DemoProduct").build();
        List<ProductTest> list = new ArrayList<>();
        writer.write(list, sheet);
        writer.finish();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcelProductTest(HttpServletResponse response, MultipartFile file) throws IOException {
        List<ProductTest> dataList = new ArrayList<ProductTest>();
        List<ProductTest> errorDatalist = new ArrayList<ProductTest>();
        final int SAVE_DB_SIZE = 20;
        EasyExcel.read(file.getInputStream(), ProductTest.class, new ReadListener<ProductTest>() {

                    /**
                     * 这个每一条数据解析都会来调用
                     * @param o
                     * @param analysisContext
                     */
                    @Override
                    public void invoke(ProductTest o, AnalysisContext analysisContext) {
//                       注意://实体对象设置 lombok 设置    @Accessors(chain = false) 禁用链式调用，否则easyexcel读取时候无法生成实体对象的值

                        int m = 0;
                        //跳过空白行,
                        if (StringUtils.isNotEmpty(o.getProductName())) {
                            dataList.add(o);
                        }

                        if (SAVE_DB_SIZE == dataList.size()) {
                            //可以设置多线程插入优化
                            //保存到数据库
                            batchInsertSession(dataList, errorDatalist);
                            dataList.clear();

                            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() ->
                            {
                                try {
                                    Thread.sleep(10 * 1000);

                                    return 1;
                                } catch (Exception ex) {
                                    return 0;
                                }

                            });
                        }


                    }

                    /**
                     *所有的都读取完 回调 ，
                     * @param analysisContext
                     */
                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                    }

                    @Override
                    public void onException(Exception exception, AnalysisContext context) throws Exception {
                        int m = 0;
//                        CellDataTypeEnum
                        throw exception;
                    }
                }
        ).sheet().doRead();

        if (dataList.size() > 0) {
            //保存到数据库
            batchInsertSession(dataList, errorDatalist);
        }

//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);
        String fileName = "DemoProduct_error" + System.currentTimeMillis();
        prepareResponds(fileName, response);

        EasyExcel.write(response.getOutputStream(), ProductTest.class).sheet("表名称").doWrite(errorDatalist);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readSpecificCell(MultipartFile[] files, TestRequest testRequest) throws Exception {
//        List<String> fileNames = saveFiles(files);
        //获取body中的参数
//            String value = (String)request.getAttribute("paramName");
        String name = testRequest.getName();
        if (files == null || files.length == 0) {
            throw new Exception("files is null");
        }
        List<String> dataList = new ArrayList<>();
        for (MultipartFile file : files) {
            try (InputStream inputStream = file.getInputStream()) {
                String targetCellValue = parseSpecificCell(inputStream, 1, 4);
                dataList.add(targetCellValue);
            } catch (Exception e) {
                throw e;
            }
        }
        String rootPath = ftpConfig.getBasePath();
        String basePath = rootPath + buildDateBasedPath();


        List<String> filePathList = new ArrayList<>();
        String filePath = "";
        for (MultipartFile file : files) {
            filePath = basePath + file.getOriginalFilename();
            Boolean success = ftpService.uploadFile(file.getBytes(), filePath);
            filePathList.add(filePath);
        }

        //保存到本地
//        List<String> fileNames = saveFiles(files);


    }

    /**
     * 构建基于日期的相对路径
     */
    public String buildDateBasedPath() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/");
        return today.format(formatter);
    }

    //    @Async
    public List<String> saveFiles(MultipartFile[] files) throws IOException {
        List<String> tempFiles = new ArrayList<String>();
        if (files != null && files.length > 0) {
            //遍历文件
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    String dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rootPath = System.getProperty("user.dir");
                    String directory = rootPath + "\\uploadfiles" + "\\" + dateStr + "\\";
//                    String directory = "\\uploadfiles" + "\\" + dateStr + "\\";
                    File destFile = new File(directory);
                    //判断路径是否存在,和C#不一样。她判断路径和文件是否存在
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }

                    //获取文件名称
                    String sourceFileName = file.getOriginalFilename();
                    //写入目的文件
                    String fileFullName = directory + sourceFileName;
                    File existFile = new File(fileFullName);
                    if (existFile.exists()) {
                        existFile.delete();
                    }
                    //用 file.transferTo 要指定盘符，不然报错找不到文件路径
                    //引入commons-io 依赖，用下面方法不用指定盘符
                    file.transferTo(existFile);
//                    FileUtils.copyInputStreamToFile(file.getInputStream(), existFile);
                    tempFiles.add(fileFullName);

                }
            }
        }
        return tempFiles;
    }

    /**
     * 解析指定单元格的值
     * @param inputStream 文件输入流
     * @param targetRow 目标行号（从0开始计数）
     * @param targetColumn 目标列号（从0开始计数，E列是第4列）
     */
    public String parseSpecificCell(InputStream inputStream, int targetRow, int targetColumn) {
        // 重置目标值
//     final   String targetCellValue = null;

        List<String> dataList = new ArrayList<>();
        // 创建读取监听器
        ReadListener<Object> listener = new ReadListener<Object>() {
            @Override
            public void invoke(Object data, AnalysisContext context) {
                // 获取当前行号（从0开始）
                int currentRowIndex = context.readRowHolder().getRowIndex();

                // 检查是否是目标行（第二行，索引为1）
                if (currentRowIndex == targetRow) {
                    if (data instanceof Map) {
//                        List<?> rowData = (List<?>) data;
//                        // 检查目标列是否存在（E列是第4列，索引为4）
//                        if (targetColumn < rowData.size()) {
//                            Object value = rowData.get(targetColumn);
//                            String targetCellValue1 = value != null ? value.toString() : null;
//                            dataList.add(targetCellValue1);
//                            System.out.println("找到目标单元格 [" + currentRowIndex + ", " + targetColumn + "] 的值: " + targetCellValue1);
//                            // 找到后立即中断读取，提高效率
//
////                            System.out.println("找到目标单元格 [" + currentRowIndex + ", " + targetColumn + "] 的值: " + targetCellValue);
////
////                            // 找到后立即中断读取，提高效率
////                            context.readSheetHolder().getReadWorkbookHolder().getReadCache().cleanAll();
////                            throw new RuntimeException("找到目标值，中断读取"); // 通过异常中断，这是常用技巧
//                        }

                        Map dataMap = (Map) data;
                        if (dataMap.keySet().contains(targetColumn)) {
                            Object value = dataMap.get(targetColumn);
                            String targetCellValue1 = value != null ? value.toString() : null;
                            if (StringUtils.isEmpty(targetCellValue1)) {
                                String msg = MessageFormat.format("Can't find cell[{0},{1}] value", currentRowIndex, targetColumn);
                                throw new RuntimeException(msg);
                            }
                            dataList.add(targetCellValue1);

                            log.info("parseSpecificCell [" + currentRowIndex + ", " + targetColumn + "] 的值: " + targetCellValue1);
                        } else {
                            String msg = MessageFormat.format("Can't find cell[{0},{1}] value", currentRowIndex, targetColumn);
                            throw new RuntimeException(msg);

                        }

                    }


                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                if (CollectionUtils.isEmpty(dataList)) {
                    System.out.println("未找到指定单元格的值");
                }
            }
        };


        EasyExcel.read(inputStream, listener)
                .sheet(0) // 读取第一个sheet（Sheet1）
                .headRowNumber(0) // 第一行是表头，从第0行开始读数据
                .doRead();

        int dataSize = dataList.size();
        String targetCellValue = dataList.get(0);
        return targetCellValue;
    }


    public int batchInsertSession(List<ProductTest> dataList, List<ProductTest> errorDatalist) {

        //
        for (ProductTest item : dataList) {
            //校验数据
            if (item.getStatus() != 1) {
                errorDatalist.add(item);
            }
        }

        dataList.removeAll(errorDatalist);

        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);) {
            //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
            ProductTestMapper mapper = sqlSession.getMapper(ProductTestMapper.class);
            //调用的是myabtis-plus的insert .可以自己写xml 的mapper insert 方法
            dataList.forEach(mapper::insert);
//        sqlSession.clearCache();
            sqlSession.commit();
        }
//        catch (Exception ex)
//        {
//            throw ex;
//        }

        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

        return 0;
    }

    private List<ProductTest> getPageData(DemoProductRequest request) {
        /**
         * 未配置拦截器 内存分页
         *
         * SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product
         *
         *  WHERE (id = 1)
         *
         *  配置mybatis-pls 拦截器之后
         *   SELECT COUNT(*) AS total FROM demo_product WHERE (id = 1)
         *  SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product
         *
         *  WHERE (id = 1) LIMIT 10
         */
        //current :pageIndex  ,size:pageSize
        Page<ProductTest> page = new Page<>(request.getPageIndex(), request.getPageSize());
        LambdaQueryWrapper<ProductTest> wrapper = new LambdaQueryWrapper<>();
        //   page 内部调用 this.getBaseMapper().selectPage
        IPage<ProductTest> productTestPage = this.page(page);
        //会执行查询count 和查询条数两个脚本
        List<ProductTest> productTestList = productTestPage.getRecords();
//        long total = productTestPage.getTotal();

        return productTestList;

        /*
        需要配置 MybatisPlusPageInterceptor 拦截器，否则是查询所有

    SELECT COUNT(*) AS total FROM demo_product WHERE (id = 1 AND product_name = 'update1')
SELECT  id,guid,product_name,product_style,image_path,create_time,modify_time,status,description,timestamp  FROM demo_product

 WHERE (id = 1 AND product_name = 'update1') LIMIT 10
         */
//        wrapper.eq(ProductTest::getProductName, "update1");
//        IPage<ProductTest> pageData = this.getBaseMapper().selectPage(page, wrapper);
//
//        int m = 0;
    }

    /**
     * 导出excel
     *
     * @param fileName
     * @param response
     * @param data     导出模板，1 导出错误信息，2 导出数据
     * @throws IOException
     */
    private void exportExcel(String fileName, HttpServletResponse response, List<GXDetailListVO> data) throws IOException {
        prepareResponds(fileName, response);
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取改类声明的所有字段
        Field[] fields = GXDetailListVO.class.getDeclaredFields();
        // 响应字段对应的下拉集合
        Map<Integer, String[]> map = new HashMap<>();
        Field field = null;
        // 循环判断哪些字段有下拉数据集，并获取
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            // 解析注解信息
            DropDownSetField dropDownSetField = field.getAnnotation(DropDownSetField.class);
            if (null != dropDownSetField) {
                String[] sources = ResoveDropAnnotationUtil.resove(dropDownSetField);
                if (null != sources && sources.length > 0) {
                    map.put(i, sources);
                }
            }
        }
        //多个sheet页写入
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.autoCloseStream(true);
//        if (flag == 0 || flag == 2) {
        builder.registerWriteHandler(new ExcelStyleConfig(Lists.newArrayList(20), null, null));
        builder.head(GXDetailListVO.class);
//        } else {
//            builder.registerWriteHandler(new ExcelStyleConfig(null,null,null));
//            builder.head(GXDetailListLogVO.class);
//        }
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "共享明细清单").build();
        builder.registerWriteHandler(new DropDownCellWriteHandler(map));
        builder.file(outputStream);

        //不能重命名，重命名就没有XLSX格式后缀
        builder.excelType(ExcelTypeEnum.XLSX);
        ExcelWriter writer = builder.build();
        writer.write(data, sheet1);
        writer.finish();

        //ExcelWriter实现Closeable 接口，内部close 调用finish, finish 内会执行关闭操作
//        outputStream.flush();
//        outputStream.close();
    }

    /**
     * 将文件输出到浏览器(导出)
     */
    private void prepareResponds(String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + ExcelTypeEnum.XLSX.getValue());

//        fileName = URLEncoder.encode(fileName, "UTF-8");
//        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setCharacterEncoding("utf-8");
//        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");


    }
    //endregion


    @Transactional(rollbackFor = Exception.class)
    public void deadLockOne() throws InterruptedException {
        Person person = personMapper.selectByPrimaryKey(1L);
        person.setName("fancky1");
        personMapper.updateByPrimaryKey(person);

        Thread.sleep(60 * 1000);

        ProductTest productTest = this.getById(1);
        LambdaUpdateWrapper<ProductTest> updateWrapper2 = new LambdaUpdateWrapper<>();
        productTest.setProductName("ProductName1");
        updateWrapper2.eq(ProductTest::getId, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re1 = this.update(productTest, updateWrapper2);
    }

    /**
     * 临间锁：降级行锁（update 当前读）,两个方法修改顺序不一致。导致两个事务内互相争取资源死锁
     * deadLockOne 先执行，成功。deadLockTwo 后执行，失败回滚。
     * mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Deadlock found when trying to get lock; try restarting transaction",
     * @throws InterruptedException
     */
    @Transactional(rollbackFor = Exception.class)
    public void deadLockTwo() throws InterruptedException {
        ProductTest productTest = this.getById(1);
        LambdaUpdateWrapper<ProductTest> updateWrapper2 = new LambdaUpdateWrapper<>();
        productTest.setProductName("ProductName1");
        updateWrapper2.eq(ProductTest::getId, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re1 = this.update(productTest, updateWrapper2);

        Thread.sleep(60 * 1000);

        Person person = personMapper.selectByPrimaryKey(1L);
        person.setName("fancky1");
        personMapper.updateByPrimaryKey(person);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eventBusTest() throws JsonProcessingException {

        BigInteger id = BigInteger.ONE;
        ProductTest productTest = this.getById(id);
        productTest.setProductStyle("getProductStyle1");
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getProductStyle, productTest.getProductStyle());
        updateWrapper3.eq(ProductTest::getId, productTest.getId());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime.now().format(dateTimeFormatter);

        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);


        //rabbitMq 发送消息线程和spring事务不在同一个线程内，mq 内部抛出异常无法被spring 事务捕获，spring 无法事务回滚
        //写入消息表

        MqMessage mqMessage = new MqMessage
                ("",
                        "",
                        "ProductTestUpdate",
                        productTest.getId().toString());
        mqMessage.setSendMq(false);
        mqMessage.setBusinessId((long) productTest.getId().intValue());

        mqMessageService.save(mqMessage);


        //            EwmsEvent event = new EwmsEvent(this, "TruckOrderComplete");
        //  busProperties.getId():  contextId, // 通常是 spring.application.name
        CustomEvent event = new CustomEvent(this, busProperties.getId(), mqMessage);
        log.info("ThreadId {} ,eventPublisher event", Thread.currentThread().getId());
        //最好使用本地消息表
        //发送消息的时候可能崩溃，不能保证消息被消费。如果发送成功了，还要设计消息表兜底失败的消息
//        MyCustomEvent event = new MyCustomEvent(busProperties.getId());
        eventPublisher.publishEvent(event);
        Object object = selfProxy;
        // 从 ApplicationContext 获取代理对象
        IProductTestService proxyService = applicationContext.getBean(IProductTestService.class);
        //AopContext.currentProxy() 返回的是当前方法调用链中的代理对象,返回的是 ScheduledTasks 的代理
        Object proxyObj = AopContext.currentProxy();
        IProductTestService productTestService = null;
        if (proxyObj instanceof IProductTestService) {
            productTestService = (IProductTestService) proxyObj;
        }
        productTestService.eventBusTest1();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eventBusTest1() {
//        int n = Integer.parseInt("m");
    }

    /**
     * 同一个事务内：可以查询到修改的数据。修改后的数据。当前行事务id就是当前事务的id  可见  mvcc
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repeatReadTest() throws InterruptedException {


        /**
         * MyBatis 在 Spring 事务内多次读取的缓存行为解析
         * MyBatis 缓存机制
         * 在 Spring 事务中使用 MyBatis 时，多次读取同一条数据是否走缓存取决于 MyBatis 的缓存配置：
         * 1. 一级缓存（Local Cache）
         * 作用范围：同一个 SqlSession
         * 生命周期：默认与事务绑定（Spring 中一个事务对应一个 SqlSession）
         * 特性：
         * 事务内多次查询相同 SQL 会走缓存
         * 执行任何 INSERT/UPDATE/DELETE 操作会清空缓存
         * 2. 二级缓存（Global Cache）
         * 作用范围：跨 SqlSession
         * 需要显式配置：在 mapper XML 中添加 <cache/> 标签
         * 生命周期：应用级别（多个事务共享）
         */
        //没有加  @Transactional 注解可以读取到其他事务修改的值 ，不能重复读取
        //加 @Transactional 注解不可以读取到其他事务修改的值，可以重复读取
        ProductTest productTest = this.getById(1);
        Thread.sleep(30 * 1000);

        //修改该行数据，可以读取到最新的，如果修改其他行数据，读取的还是缓存。
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, 3);
        //如果修改id=2 ，读取的还是缓存
        updateWrapper3.eq(ProductTest::getId, 1);

        //update delete insert 当前读
        //执行任何 INSERT/UPDATE/DELETE 操作都会清空当前 SqlSession 的一级缓存

        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);
        //修改该行数据，可以读取到最新的

        ProductTest productTest1 = this.getById(1);

        int m = 0;


//        Object proxyObj = AopContext.currentProxy();
//        IProductTestService productTestService = null;
//        if (proxyObj instanceof IProductTestService) {
//            productTestService = (IProductTestService) proxyObj;
//        }
//        for (int i = 0; i < 3; i++) {
//            productTestService.repeatReadFun();
//        }
    }

    /**
     * MyBatis 缓存 vs 事务隔离：MyBatis 的一级缓存可能会影响你观察到的事务隔离行为，特别是在同一事务中多次执行相同查询时
     * @throws InterruptedException
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void repeatReadMultiTransaction() throws InterruptedException {

//        Spring 的 @Transactional 注解如果不显式指定隔离级别，默认使用：Isolation.DEFAULT

//        MySQL/InnoDB：REPEATABLE READ（可重复读）
//        Oracle：READ COMMITTED（读已提交）
//        PostgreSQL：READ COMMITTED（读已提交）
//        SQL Server：READ COMMITTED（读已提交）
//        H2：READ COMMITTED（读已提交）

        //查看mysql事务隔离级别
//        SHOW GLOBAL VARIABLES LIKE 'transaction_isolation'  REPEATABLE-READ


//
//        MyBatis 一级缓存：MyBatis 默认开启一级缓存（SqlSession 级别），在同一 SqlSession 中，相同的查询会直接从缓存返回结果，而不会再次访问数据库。
//        Spring 事务管理：在 Spring 管理的事务中，默认情况下整个事务方法使用同一个 SqlSession，这会导致在事务内部多次执行相同查询时，MyBatis 直接从缓存返回数据。
//
        try (Connection conn = dataSource.getConnection()) {
//            DEFAULT(-1),
//                    READ_UNCOMMITTED(1),
//                    READ_COMMITTED(2),
//                    REPEATABLE_READ(4),
//                    SERIALIZABLE(8);
            //4
            int level = conn.getTransactionIsolation();
            int n = 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //两次读取的值一样，尽管期间其他线程的事务修改了值。 mybatis 缓存读取 一级缓存（Spring 中一个事务对应一个 SqlSession）
        //
        ProductTest productTest = this.getById(1);
        int version = productTest.getVersion();
        Thread.sleep(30 * 1000);
        ProductTest productTest1 = this.getById(1);
        int version1 = productTest1.getVersion();

        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getProductName, "ddd");
        //如果修改id=2 ，读取的还是缓存。
        // 修改该条数据导致缓存失效从数据库读取,可以读取到事务期间其他事务的更新
        updateWrapper3.eq(ProductTest::getId, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);


//        可以通过配置参数禁用此行为(需要重启MySQL):
//[mysqld]
//innodb_locks_unsafe_for_binlog = 0
//        行为特征	标准REPEATABLE READ	半一致性读
//        SELECT查询	使用事务开始时的读视图	不适用
//        UPDATE/DELETE	应使用读视图	可能使用最新提交版本
//        数据一致性	严格可重复读	可能看到更新后的数据
//        并发性能	较低	更高

//        半一致性读是MySQL InnoDB存储引擎在REPEATABLE READ隔离级别下对UPDATE语句的一种特殊优化机制。
//        它允许UPDATE语句在某些条件下查看其他事务已提交的最新数据版本，
//        而不是严格遵循事务开始时建立的读视图
        //可以读取到其他事务更新的
        ProductTest productTest2 = this.getById(1);
        int version2 = productTest2.getVersion();

        int m = 0;


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repeatReadMultiTransactionUpdate() {
        //修改该行数据，可以读取到最新的，如果修改其他行数据，读取的还是缓存。
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, 3);
        //如果修改id=2 ，读取的还是缓存
        updateWrapper3.eq(ProductTest::getId, 1);
        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);
    }

    @Transactional(rollbackFor = Exception.class)
    public void repeatReadFun() {
        ProductTest productTest = this.getById(1);
        int version = productTest.getVersion() == null ? 0 : productTest.getVersion();
        version++;
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, version);
        updateWrapper3.eq(ProductTest::getId, 1);

        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);
    }


    /**
     * 非事务方法，直接调用事务方法，事务方法会失效不走事务aop
     * 务方法，直接调用事务方法，两个事务都在调用方的事务内执行。被调用方法没有走aop事务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionalFunTest() {
        ProductTest productTest = this.getById(1);

        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, 98);
        updateWrapper3.eq(ProductTest::getId, 1);
        boolean re3 = this.update(updateWrapper3);

        //被调用方不走aop事务 ：不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
        //transactionalFunTest 不加@Transactional 注解， transactionalFun 可以保存成功，因为是独立的新事务
        //transactionalFunTest加注解，transactionalFun不加注解，两个方法都在调用方的事务内执行
        transactionalFun();
        int m = Integer.parseInt("m");

        //被调用方不走aop事务 ：不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
//        transactionalFun();
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, 98);
//        updateWrapper3.eq(ProductTest::getId, 1);
//        boolean re3 = this.update(updateWrapper3);
//        //不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
//
//        int m = Integer.parseInt("m");

        //被调用方不走aop事务 ：不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
        //   不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
//        transactionalFunRequiresNew();
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, 98);
//        updateWrapper3.eq(ProductTest::getId, 1);
//        boolean re3 = this.update(updateWrapper3);
//        //不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
//
//        int m = Integer.parseInt("m");


//        //被调用方会触发事务aop, 两个方法在不同事务内
//        Object proxyObj = AopContext.currentProxy();
//        IProductTestService productTestService = null;
//        if (proxyObj instanceof IProductTestService) {
//            productTestService = (IProductTestService) proxyObj;
//        }
//        productTestService.transactionalFunRequiresNew();
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, 98);
//        updateWrapper3.eq(ProductTest::getId, 1);
//        boolean re3 = this.update(updateWrapper3);
//        //不会触发事务管理的AOP切面。这意味着methodB的事务配置不会生效，因为它被视为methodA的一部分。
//
//        int m = Integer.parseInt("m");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionalFun() {
        //从applicationContext 获取的bean 有完整的 Advisor ，selfProxy 是提前暴露的半成品Advisor不全
        IProductTestService service = applicationContext.getBean(IProductTestService.class);
        ProductTest productTest = this.getById(1);

        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, 99);
        updateWrapper3.eq(ProductTest::getId, 1);
        boolean re3 = this.update(updateWrapper3);
//        int m= Integer.parseInt("m");
//        mqSendUtil.releaseLock(null,true);

        boolean isActualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();

        //spring 定时任务调用事务方法也可以开启事务
        //true。非事务方法调用事务方法：事务方法可以开启事务
        boolean isSynchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        String threadName = Thread.currentThread().getName();
        int m = 0;

    }

    @Override
    @Async("mqFailHandlerExecutor")
    public void transactionalFunAsyncWrapper() {
        //        异步方法内不能直接使用 AopContext.currentProxy()
//        AopContext.currentProxy() 是线程绑定的，不能在异步线程中直接使用
//        // AopContext内部使用ThreadLocal存储当前代理
//        private static final ThreadLocal<Object> currentProxy = new ThreadLocal<>();
//        异步方法内需要重新获取代理，通过 ApplicationContext.getBean()
        //从applicationContext 获取的bean 有完整的 Advisor ，selfProxy 是提前暴露的半成品Advisor不全
        IProductTestService service = applicationContext.getBean(IProductTestService.class);
        //true
        boolean proxy = AopUtils.isAopProxy(selfProxy);

        // 1. 检查代理类型
        //true
        boolean isAopProxy = AopUtils.isAopProxy(selfProxy);
        //false
        boolean isCglibProxy = AopUtils.isCglibProxy(selfProxy);
        //true
        boolean isJdkProxy = AopUtils.isJdkDynamicProxy(selfProxy);
        //还是开启了事务方法
        selfProxy.transactionalFun();
    }

    /**
     * 非事务方法调用事务方法：事务方法可以开启事务
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void noTranMethodCallTranMethod() {

//        ProductTest productTest = this.getById(1);
//
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, 99);
//        updateWrapper3.eq(ProductTest::getId, 1);
//        boolean re3 = this.update(updateWrapper3);

        //从applicationContext 获取的bean 有完整的 Advisor ，selfProxy 是提前暴露的半成品Advisor不全
        IProductTestService service = applicationContext.getBean(IProductTestService.class);
//        selfProxy.transactionalFun();
//        int m= Integer.parseInt("m");
        selfProxy.transactionalFunAsyncWrapper();

    }


    /**
     * REQUIRES_NEW:开启新的事物，调用方和被调用方在不同的事务内，互不影响
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void transactionalFunRequiresNew() {
        ProductTest productTest = this.getById(1);

        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getVersion, 99);
        updateWrapper3.eq(ProductTest::getId, 1);
        boolean re3 = this.update(updateWrapper3);
    }

    @Override
    public boolean batchUpdateByCondition() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            LambdaQueryWrapper<ProductTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.le(ProductTest::getId, 1);
            List<ProductTest> productTestList = this.list(queryWrapper);


            ProductTestMapper mapper = sqlSession.getMapper(ProductTestMapper.class);
            int i = 0;
            for (ProductTest entity : productTestList) {
                LambdaUpdateWrapper<ProductTest> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ProductTest::getId, entity.getId())
                        .set(ProductTest::getProductName, entity.getProductName() + 1)
                        .set(ProductTest::getDescription, entity.getDescription());
                //
//                Mapper 的 update 方法的返回值是一个 int 类型，表示受影响的行数。具体来说：
//                如果返回值大于 0，表示更新成功，且返回值是实际更新的记录数。
//                如果返回值等于 0，表示没有记录被更新（可能是没有匹配的条件）。
//                如果返回值小于 0，通常表示更新失败（可能是 SQL 执行错误）。
                //事务未提交 返回-2147482646
                int affectRows = mapper.update(null, updateWrapper);


                i++;
            }
            sqlSession.commit(); // 提交事务
            return true;
        } catch (Exception e) {
            sqlSession.rollback(); // 回滚事务
            return false;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchUpdateBySelective() throws Exception {
        //在LogAspect 已加锁
        String lockKey = ConfigConst.DEMO_PRODUCT_PREFIX + "batchUpdateBySelective";
        RLock lock = redissonClient.getLock(lockKey);
        try {

            boolean lockSuccessfully = lock.tryLock(30, 60, TimeUnit.SECONDS);
            if (!lockSuccessfully) {
                log.info("Thread - {} 获得锁 {}失败！锁被占用！", Thread.currentThread().getId(), lockKey);
                return null;
            }
            LambdaQueryWrapper<ProductTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.le(ProductTest::getId, 100);
            List<ProductTest> productTestList = this.list(queryWrapper);


            for (ProductTest productTest : productTestList) {
                productTest.setProductName(productTest.getProductName() + 1);
                productTest.setProductStyle(productTest.getProductStyle() + 1);
            }

//        modify_time=now() 脚本添加了修改时间 ，始终会有影响行数
            //去掉 modify_time=now() ，数据不做任何修改返回0
            int affectRows = 0;
            if (productTestList.size() > 0) {
                affectRows = this.baseMapper.batchUpdateBySelective(productTestList);
            } else {
                return 0;
            }

            if (affectRows != productTestList.size()) {
                //事务回滚 手动回滚事务 手动提交事务
                //事务回滚 手动回滚 手动控制事务，编程式事务
                //TransactionAspectSupport
                //PlatformTransactionManager 参见  com.example.demo.service.demo.PersonService
                //TransactionTemplate提供了更简洁的API来管理事务。它隐藏了底层的PlatformTransactionManager的使用
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

//            throw  new Exception("更新失败");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            //unlock 删除key
            //如果锁因超时（leaseTime）会抛异常
            lock.unlock();
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionalCallBack() throws Exception {
        for (int i = 1; i < 3; i++) {
            transactionalCallBack2(BigInteger.valueOf(i));
        }
    }


    /**
     * 所有事务提交了才会执行 事务回调
     * @param productId
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void transactionalCallBack2(BigInteger productId) throws Exception {
        ProductTest productTest = this.getById(productId);
        if (productId.compareTo(BigInteger.valueOf(2)) == 0) {
//            productTest.setId(BigInteger.valueOf(0));
        }
        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getProductName, productTest.getProductName() + productId);
        updateWrapper3.eq(ProductTest::getId, productTest.getId());
        //如果值不改变，update 返回false.其内部根据mapper 执行的结果判断： result >= 1
        // this.update(updateWrapper3);内部调用 update(null,updateWrapperOne);
        boolean re3 = this.update(null, updateWrapper3);
        if (!re3) {
            throw new Exception("error");
        }

        MqMessage mqMessage = new MqMessage
                (RabbitMQConfig.BATCH_DIRECT_EXCHANGE_NAME,
                        RabbitMQConfig.BATCH_DIRECT_ROUTING_KEY,
                        RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME,
                        productTest.getId().toString());
        mqMessageService.add(mqMessage);
//        mqSendUtil.send(mqMessage);
    }


    /*
LockAnnotationAdvisor 实现了Ordered接口
    Lock4j 内部配置类LockAutoConfiguration注册bean LockAnnotationAdvisor 时候设置order =Integer.MIN_VALUE
     return new LockAnnotationAdvisor(lockInterceptor, Integer.MIN_VALUE);

     从而保证@ Lock4j 优先@Transactional 切面先执行

//    如果在service上有@Transactional和@lock4j，则执行顺序如下
//
//# 1. 上锁
//# 2. 开启事务
//# 3. 执行逻辑
//# 4. 提交/回滚事务
//# 5. 释放锁
@Lock4j 的 key 是静态的，但可以通过 SpEL（Spring Expression Language） 或自定义逻辑实现动态 key。
使用 SpEL 动态设置 key
@Lock4j(key = "'lock:' + #userId + ':' + #orderId") // 组合多个参数
    public void doSomething(String userId, String orderId)


     @Lock4j  无法灵活的设置要锁的key，设置静态key 可以简化代码
*/
//    @Lock4j(keys = {"#key"}, acquireTimeout = 1000, expire = 6000)

    /**
     * 解决并发下 redissonLock 释放了 事务未提交
     * @param i
     * @throws InterruptedException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionalRedission(int i) throws InterruptedException {
        //不加锁产生线程安全问题：0-->1,而不是10
//        ProductTest productTest = this.getById(2);
//        productTest.setVersion(productTest.getVersion() + 1);
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, productTest.getVersion());
//        updateWrapper3.eq(ProductTest::getId, productTest.getId());
//        this.update(updateWrapper3);
//        Thread.sleep(3 * 1000);


        //redisson 锁也会产生并发问题，因为Transactional 是aop,获取锁时候事务未提交，读取的还是修改前的值
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String operationLockKey = RedisKeyConfigConst.MQ_FAIL_HANDLER;
        //并发访问，加锁控制
        RLock lock = redissonClient.getLock(operationLockKey);
        boolean lockSuccessfully = false;
        try {
            long waitTime = 200;
            long leaseTime = 300;
            //            lockSuccessfully = lock.tryLock();
//            lock.tryLock(waitTime, TimeUnit.SECONDS);
//            lock.lock(leaseTime, TimeUnit.SECONDS);
            lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (lockSuccessfully) {


                ProductTest productTest = this.getById(2);
                log.info("BeforeVersion{} - {}", i, productTest.getVersion());
                productTest.setVersion(productTest.getVersion() + 1);
                LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
                updateWrapper3.set(ProductTest::getVersion, productTest.getVersion());
                updateWrapper3.eq(ProductTest::getId, productTest.getId());
                this.update(updateWrapper3);
                log.info("AfterVersion{} - {}", i, productTest.getVersion());
//                Thread.sleep(3 * 1000);
                //代码处理异常不会进入事务完成的方法，要在catch 内释放锁
//                int m = Integer.parseInt("d");
                //事务完成会吊钟释放锁
//                mqSendUtil.releaseLock(lock,lockSuccessfully);


            } else {
                //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                //超过waitTime ，扔未获得锁
                log.info("获取锁失败");
            }
        } catch (Exception e) {
            //代码处理异常不会进入事务完成的方法，要在catch 内释放锁
            lock.unlock();
            throw e;
//            log.error("", e);
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            //unlock 删除key
//            lock.unlock();

//            if (lockSuccessfully) {
//                try {
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                    }
//                } catch (Exception e) {
//                    log.warn("Redis check lock ownership failed: ", e);
//                }
//            }

            mqSendUtil.releaseLock(lock, lockSuccessfully);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transactionalRedissonForShare(BigInteger id) throws InterruptedException {
        //不加锁产生线程安全问题：0-->1,而不是10
//        ProductTest productTest = this.getById(2);
//        productTest.setVersion(productTest.getVersion() + 1);
//        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
//        updateWrapper3.set(ProductTest::getVersion, productTest.getVersion());
//        updateWrapper3.eq(ProductTest::getId, productTest.getId());
//        this.update(updateWrapper3);
//        Thread.sleep(3 * 1000);


        ProductTest productTest1 = this.getBaseMapper().selectById(id);

        //redisson 锁也会产生并发问题，因为Transactional 是aop,获取锁时候事务未提交，读取的还是修改前的值
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String operationLockKey = RedisKeyConfigConst.MQ_FAIL_HANDLER;
        //并发访问，加锁控制
        RLock lock = redissonClient.getLock(operationLockKey);
        boolean lockSuccessfully = false;
        try {
            long waitTime = 200;
            long leaseTime = 300;
//            lockSuccessfully = lock.tryLock();
//            lock.tryLock(waitTime, TimeUnit.SECONDS);
//            lock.lock(leaseTime, TimeUnit.SECONDS);
            lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);


            if (lockSuccessfully) {

                //读取还是myabtis 一级缓存和productTest1的值一样
                ProductTest productTest2 = this.getBaseMapper().selectById(id);
                //当前读，可以读取到最新的数据
//                ProductTest productTest3=  this.getBaseMapper().getByIdForShareMySql(id);
                ProductTest productTest3 = getByIdLastForShareMySql(id);

                ProductTest productTest = productTest3;/// this.getById(2);
                log.info("BeforeVersion{} - {}", id, productTest.getVersion());
                productTest.setVersion(productTest.getVersion() + 1);
                LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
                updateWrapper3.set(ProductTest::getVersion, productTest.getVersion());
                updateWrapper3.eq(ProductTest::getId, productTest.getId());
                this.update(updateWrapper3);
                log.info("AfterVersion{} - {}", id, productTest.getVersion());
//                Thread.sleep(3 * 1000);
                //代码处理异常不会进入事务完成的方法，要在catch 内释放锁
//                int m = Integer.parseInt("d");
                //事务完成会吊钟释放锁
//                mqSendUtil.releaseLock(lock);


            } else {
                //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                //超过waitTime ，扔未获得锁
//                直接返回 false 的情况：
//                当锁已经被其他线程持有，并且该锁没有设置过期时间(leaseTime)时
//                或者锁虽然设置了 leaseTime，但剩余存活时间超过您指定的 leaseTime 参数
                log.info("获取锁失败");
                if (!lockSuccessfully) {
                    // 检查锁剩余时间
                    long remainingTTL = lock.remainTimeToLive();
                    System.out.println("锁剩余时间: " + remainingTTL + "ms");
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            Thread.sleep(30 * 1000);
            mqSendUtil.releaseLock(lock, lockSuccessfully);
        }
    }


    @Override
    @PointcutExecuteOrderTwo
    @PointcutExecuteOrderOne
    public void pointcutExecuteOrder() {
        log.info("pointcutExecuteOrder - service");
    }

    @Override
    public void tryThrowStackTrace() {
        int m = Integer.parseInt("m");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void mqMessageConsume(MqMessage message) {
        int m = Integer.parseInt("m");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer mqMessageOperation() throws JsonProcessingException {

        ProductTest productTest = new ProductTest();
        String uuid = UUID.randomUUID().toString();
//            uuid = "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
//                    + "dssssssssssssssssssssssssssssssssssssssssss";
        productTest.setGuid(uuid);
        productTest.setProductName("productNameshish事务");
        productTest.setProductStyle("productStyle");
        productTest.setImagePath("D:\\fancky\\git\\Doc");
        productTest.setCreateTime(LocalDateTime.now());
        productTest.setModifyTime(LocalDateTime.now());
        productTest.setStatus(1);
        productTest.setDescription("setDescription_sdsdddddddddddddddd");
        productTest.setTimestamp(LocalDateTime.now());
        int i = baseMapper.insert(productTest);


        String msgContent = objectMapper.writeValueAsString(productTest);
        //rabbitMq 发送消息线程和spring事务不在同一个线程内，mq 内部抛出异常无法被spring 事务捕获，spring 无法事务回滚
        MqMessage mqMessage = new MqMessage
                (RabbitMQConfig.DIRECT_EXCHANGE_NAME,
                        RabbitMQConfig.DIRECT_ROUTING_KEY,
                        RabbitMQConfig.DIRECT_QUEUE_NAME,
                        msgContent);
        mqMessageService.save(mqMessage);

        return 0;
    }

    @Override
    public ProductTest getByIdForShareMySql(BigInteger id) {
        return this.getBaseMapper().getByIdForShareMySql(id);
    }

    @Override
    public ProductTest getByIdLastForShareMySql(BigInteger id) {
        LambdaQueryWrapper<ProductTest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductTest::getId, id);
        //此处根据 数据动态拼接 last.last  会拼接在DeletedInnerInterceptor 里拼接的语句前面
        queryWrapper.last(" for share;");
        return this.getBaseMapper().selectOne(queryWrapper);
    }
}
