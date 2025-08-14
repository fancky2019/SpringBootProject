package com.example.demo.dao.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

/**
 * 注：手动添加@mapper注解否则iml类找不到bean报错
 *
 * @author author
 * @since 2022-11-17
 */
@Mapper
public interface ProductTestMapper extends BaseMapper<ProductTest> {
    void truncateTest();

    BigInteger getMaxId();

    //    ProductTest getById(BigInteger id);
    int updateByPrimaryKeySelective(ProductTest productTest);

    int batchUpdateBySelective(List<ProductTest> productTests);


    List<ProductTest> getByIds(List<BigInteger> ids);

    ProductTest getByIdForShareMySql(BigInteger id);

    List<ProductTest> getPageData(DemoProductRequest request);

    List<ProductTest> getPageDataOptimization(DemoProductRequest request);

}
