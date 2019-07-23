package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Menus;
import com.example.demo.model.viewModel.rabc.MenusVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
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