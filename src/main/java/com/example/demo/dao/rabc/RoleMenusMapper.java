package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.RoleMenus;

public interface RoleMenusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleMenus record);

    int insertSelective(RoleMenus record);

    RoleMenus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenus record);

    int updateByPrimaryKey(RoleMenus record);
}