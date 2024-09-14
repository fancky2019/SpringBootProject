package com.example.demo.dao.demo;

import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.request.DemoProductRequest;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface DemoProductMapper {

    List<DemoProduct> getPageData(DemoProductRequest request);

    List<DemoProduct> query(DemoProductRequest request);

    int deleteByPrimaryKey(Integer id);

    int insert(DemoProduct record);

    int insertSelective(DemoProduct record);

    DemoProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DemoProduct record);

    int updateByPrimaryKey(DemoProduct record);

    int batchInsert(List<DemoProduct> list);

    int batchUpdate(List<DemoProduct> list);

    int batchDelete(List<Integer> list);

    BigInteger getMaxId();

    ProductTest getById(BigInteger id);

    List<ProductTest> getByIds(List<BigInteger> ids);

    int batchUpdateProductTest(List<ProductTest> list);
}