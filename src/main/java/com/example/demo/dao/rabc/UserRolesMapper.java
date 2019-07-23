package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.UserRoles;
import com.example.demo.model.viewModel.rabc.UserRolesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
public interface UserRolesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRoles record);

    int insertSelective(UserRoles record);

    UserRoles selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserRoles record);

    int updateByPrimaryKey(UserRoles record);

    List<UserRolesVM> getPageData(UserRolesVM viewModel);

    Integer getPageDataCount(UserRolesVM viewModel);
}