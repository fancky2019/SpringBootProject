package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.ProductVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    List<UsersVM> getPageData(UsersVM viewModel);
}