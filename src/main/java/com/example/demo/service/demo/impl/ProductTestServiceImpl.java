package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.service.demo.IProductTestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2022-11-17
 */
@Service
public class ProductTestServiceImpl extends ServiceImpl<ProductTestMapper, ProductTest> implements IProductTestService {

    private ProductTestMapper productTestMapper;

    public ProductTestServiceImpl(ProductTestMapper productTestMapper) {
        this.productTestMapper = productTestMapper;
    }


    @Override
    public void mybatisPlusTest() {

//        this.baseMapper.deleteBatchIds();
//        this.saveEntity();
//        saveOrUpdateBatch();
//        queryTest();
//        truncateTest();
//        deleteTableDataTest();
        selectMaxId();
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

    private void saveOrUpdateBatch() {
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
//        this.saveBatch()；
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


        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
//        queryWrapper.select("","");
//        queryWrapper.eq("",1);
//        queryWrapper.ne();
        String productName = "";
        //有条件拼接条件
        queryWrapper.eq(StringUtils.isNotEmpty(productName), "product_name", productName);
//        queryWrapper.orderByDesc("desc");
        List<ProductTest> list = this.list(queryWrapper);

        LambdaQueryWrapper<ProductTest> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProductTest::getProductName, "productName_xiugai55555");

        List<ProductTest> list1 = this.list(lambdaQueryWrapper);


        ProductTestMapper productTestMapper = this.getBaseMapper();
        //链式查询方式
        List<ProductTest> list2 = new LambdaQueryChainWrapper<ProductTest>(this.getBaseMapper())
                .eq(ProductTest::getProductName, "productName_xiugai55555")
                .list();
        int m = 0;
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

    /*
    更新表的指定字段
     */
    private void updateField() {
        UpdateWrapper<ProductTest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("ID", 0);//条件
        updateWrapper.set("SESSION_KEY", "abc");//要更新的列
        //  实体要指定null ，不然默认更新非空字段
        baseMapper.update(null, updateWrapper);


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

}
