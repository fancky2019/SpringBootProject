package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.service.demo.IProductTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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


    @Override
    public void mybatisPlusTest() {

//        this.baseMapper.deleteBatchIds();
//        this.saveEntity();
//        saveOrUpdateBatch();
        queryTest();
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
        productTest.setId(100001);
        productTest.setGuid("dssddssd");
        productTests.add(productTest);

        productTest = new ProductTest();
        productTest.setId(100002);
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
        QueryWrapper<ProductTest> queryWrapper = new QueryWrapper<ProductTest>();
//        queryWrapper.select("","");
//        queryWrapper.eq("",1);
//        queryWrapper.ne();
        queryWrapper.eq("product_name", "productName_xiugai55555");
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


}
