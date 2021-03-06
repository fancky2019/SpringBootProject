package com.example.demo.dao.mybatissql;

import com.example.demo.model.viewModel.ProductVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MybatisSqlMapper {


//    List<ProductVM> getPageData(String sql);

    //要指定参数名称 param("mapper内名称")
    List<ProductVM> getPageData(@Param("sql") String sql);

    List<ProductVM> getPageDataByHelper(ProductVM productVM);
    List<ProductVM> getPageDataByHelperCTE(ProductVM productVM);

    List<ProductVM> concatSelect(ProductVM productVM);

}
