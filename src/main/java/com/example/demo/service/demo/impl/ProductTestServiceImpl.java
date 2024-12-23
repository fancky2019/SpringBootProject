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
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.easyexcel.DropDownSetField;
import com.example.demo.easyexcel.ExcelStyleConfig;
import com.example.demo.easyexcel.GXDetailListVO;
import com.example.demo.easyexcel.ResoveDropAnnotationUtil;
import com.example.demo.easyexcel.handler.DropDownCellWriteHandler;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.pojo.Student;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.service.demo.IProductTestService;
import com.example.demo.service.wms.ProductService;
import com.example.demo.utility.ConfigConst;
import com.example.demo.utility.ExcelUtils;
import com.example.demo.utility.MqSendUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2022-11-17
 */
@Service
@Slf4j
public class ProductTestServiceImpl extends ServiceImpl<ProductTestMapper, ProductTest> implements IProductTestService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ProductTestMapper productTestMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductService productService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @Autowired
    private MqSendUtil mqSendUtil;


    public ProductTestServiceImpl(ProductTestMapper productTestMapper) {
        this.productTestMapper = productTestMapper;
    }


    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void mybatisPlusTest() throws InterruptedException {

//        this.baseMapper.deleteBatchIds();
//        this.saveEntity();
//        saveOrUpdateBatch();
//        updateBatchTest();
        queryById();
//        queryTest();
//        updateTest();
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

    /**
     * 耗时108ms
     * 会更新所有字段  ,更新部分字段可以大大减少耗时
     */
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
        //批量更新还是要加锁，避免并发访问

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
//        boolean result = this.saveOrUpdateBatch(list);


        boolean result = this.updateBatchById(list);
        stopWatch.stop();
        //        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

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
        boolean re3 = this.update(updateWrapper3);

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
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateField() {


        LambdaUpdateWrapper<ProductTest> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.set(ProductTest::getProductStyle, "getProductStyle111");
        updateWrapper3.eq(ProductTest::getId, 1);

        //更新指定条件的 为productTest 对象的值，ID 字段除外。
        boolean re3 = this.update(updateWrapper3);

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

    }

    //region redis

    /**
     * * 雪崩：随机过期时间
     * * 击穿：分布式锁（表名），没有取到锁，sleep(50)+重试
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
                    return null;
                }
                BigInteger idB = BigInteger.valueOf(id);
                ProductTest productTest = this.baseMapper.getById(idB);
                //穿透：设置个空值
                if (productTest == null) {
                    valueOperations.set(key, ConfigConst.EMPTY_VALUE);
                    redisTemplate.expire(key, 60, TimeUnit.SECONDS);
                } else {
                    String json = objectMapper.writeValueAsString(productTest);
                    valueOperations.set(key, json);
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

        }
        else {
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
            request.setPageIndex(i);
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
}
