package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Authorities;

public interface AuthoritiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Authorities record);

    int insertSelective(Authorities record);

    Authorities selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Authorities record);

    int updateByPrimaryKey(Authorities record);
}