package com.example.demo.dao.wms;

import com.example.demo.model.entity.wms.Product;
import com.example.demo.model.viewModel.ProductVM;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKeyWithBLOBs(Product record);

    int updateByPrimaryKey(Product record);

    //HashMap
    List<Product> getProductProc(HashMap<String, Object> map);

    //对象
    List<Product> getProductProc(Product Product);

    List<ProductVM> getPageData(ProductVM viewModel);
}