package com.example.demo.dao.demo;

import com.example.demo.model.entity.demo.Person;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PersonMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Person record);


//    int insertSelective(Person record);
//
    Person selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(Person record);
//
    int updateByPrimaryKey(Person record);

   List<Person> selectByName(String name);

}