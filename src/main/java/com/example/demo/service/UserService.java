package com.example.demo.service;

import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    UsersMapper usersMapper;

    public Users selectByPrimaryKey(int id) {
        return usersMapper.selectByPrimaryKey(id);
    }
}
