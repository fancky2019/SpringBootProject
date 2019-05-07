package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.viewModel.rabc.RolesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface RolesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Roles record);

    int insertSelective(Roles record);

    Roles selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Roles record);

    int updateByPrimaryKey(Roles record);

    List<RolesVM> getPageData(RolesVM viewModel);

    Integer getPageDataCount(RolesVM viewModel);
}