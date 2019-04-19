package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Menus;

public interface MenusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Menus record);

    int insertSelective(Menus record);

    Menus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menus record);

    int updateByPrimaryKey(Menus record);
}