package com.example.demo.service.shiro;

import com.example.demo.dao.shiro.UserMapper;
import com.example.demo.model.entity.shiro.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("shiroUserService")
public class UserService {
    @Autowired
    UserMapper userMapper;

    public int insert(User user)
    {
        return userMapper.insert(user);
    }

    public  User selectByUserName(String username)
    {
        return userMapper.selectByUserName(username);
    }
}
