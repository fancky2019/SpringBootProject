package com.example.demo.dao.shiro;

import com.example.demo.model.entity.shiro.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    //    int deleteByPrimaryKey(UserKey key);
    User selectByUserName(String username);

    int insert(User record);

    int insertSelective(User record);

//    User selectByPrimaryKey(UserKey key);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
