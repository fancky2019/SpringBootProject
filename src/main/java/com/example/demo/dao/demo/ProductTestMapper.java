package com.example.demo.dao.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.demo.ProductTest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 注：手动添加@mapper注解否则iml类找不到bean报错
 *
 * @author author
 * @since 2022-11-17
 */
@Mapper
public interface ProductTestMapper extends BaseMapper<ProductTest> {

}
