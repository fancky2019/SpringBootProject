package com.example.demo.dao;

import com.example.demo.model.entity.Product;
import com.example.demo.model.viewModel.ProductVM;

import java.util.HashMap;
import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKeyWithBLOBs(Product record);

    int updateByPrimaryKey(Product record);

    //HashMap
    List<Product> getProductProc(HashMap<String,Object> map);
    //对象
    List<Product> getProductProc(Product Product);

    List<ProductVM> getPageData(ProductVM viewModel);
}