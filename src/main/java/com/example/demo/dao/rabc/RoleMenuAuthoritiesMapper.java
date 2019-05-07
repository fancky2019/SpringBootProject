package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.RoleMenuAuthorities;
import com.example.demo.model.viewModel.rabc.RoleMenuAuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface RoleMenuAuthoritiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleMenuAuthorities record);

    int insertSelective(RoleMenuAuthorities record);

    RoleMenuAuthorities selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenuAuthorities record);

    int updateByPrimaryKey(RoleMenuAuthorities record);

    List<RoleMenuAuthoritiesVM> getPageData(RoleMenuAuthoritiesVM viewModel);

    Integer getPageDataCount(RoleMenuAuthoritiesVM viewModel);
}