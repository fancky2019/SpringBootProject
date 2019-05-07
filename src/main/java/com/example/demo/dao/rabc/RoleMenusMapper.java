package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.RoleMenus;
import com.example.demo.model.viewModel.rabc.RoleMenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface RoleMenusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleMenus record);

    int insertSelective(RoleMenus record);

    RoleMenus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenus record);

    int updateByPrimaryKey(RoleMenus record);

    List<RoleMenusVM> getPageData(RoleMenusVM viewModel);

    Integer getPageDataCount(RoleMenusVM viewModel);
}