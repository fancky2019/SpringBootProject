package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Roles;
import com.example.demo.model.viewModel.rabc.RolesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
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