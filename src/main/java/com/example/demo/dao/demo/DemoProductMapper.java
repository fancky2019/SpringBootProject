package com.example.demo.dao.demo;

import com.example.demo.model.entity.demo.DemoProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DemoProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DemoProduct record);

    int insertSelective(DemoProduct record);

    DemoProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DemoProduct record);

    int updateByPrimaryKey(DemoProduct record);

    int batchInsert(List<DemoProduct> list);

}