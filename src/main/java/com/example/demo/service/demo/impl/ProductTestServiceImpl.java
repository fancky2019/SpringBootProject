package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dao.demo.ProductTestMapper;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.service.demo.IProductTestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
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
        this.saveEntity();
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
}
