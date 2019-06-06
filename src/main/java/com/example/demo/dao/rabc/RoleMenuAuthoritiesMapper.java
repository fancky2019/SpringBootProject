package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.RoleMenuAuthorities;
import com.example.demo.model.viewModel.rabc.RoleMenuAuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
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