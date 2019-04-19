package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.RoleMenuAuthorities;

public interface RoleMenuAuthoritiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleMenuAuthorities record);

    int insertSelective(RoleMenuAuthorities record);

    RoleMenuAuthorities selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenuAuthorities record);

    int updateByPrimaryKey(RoleMenuAuthorities record);
}