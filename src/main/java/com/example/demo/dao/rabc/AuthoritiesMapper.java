package com.example.demo.dao.rabc;

import com.example.demo.model.entity.rabc.Authorities;
import com.example.demo.model.viewModel.rabc.AuthoritiesVM;
import com.example.demo.model.viewModel.rabc.UsersVM;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
Mapper加@Repository注解Service层依赖注入ProductMapper就不会报错红丝波浪线（其实没有错）。
 */
@Repository
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