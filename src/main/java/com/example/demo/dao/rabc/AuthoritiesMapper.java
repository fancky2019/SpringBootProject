package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Authorities;
import com.example.demo.model.viewModel.rabc.AuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface AuthoritiesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Authorities record);

    int insertSelective(Authorities record);

    Authorities selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Authorities record);

    int updateByPrimaryKey(Authorities record);

    List<AuthoritiesVM> getPageData(AuthoritiesVM viewModel);

    Integer getPageDataCount(AuthoritiesVM viewModel);
}