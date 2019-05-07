package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Menus;
import com.example.demo.model.viewModel.rabc.MenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;

import java.util.List;

public interface MenusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Menus record);

    int insertSelective(Menus record);

    Menus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menus record);

    int updateByPrimaryKey(Menus record);

    List<MenusVM> getPageData(MenusVM viewModel);

    Integer getPageDataCount(MenusVM viewModel);
}