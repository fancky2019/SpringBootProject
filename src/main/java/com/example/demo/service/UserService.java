package com.example.demo.service;

import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.VoidDescriptor;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;


@Service
public class UserService {

    @Autowired
    UsersMapper usersMapper;


    public Users selectByPrimaryKey(int id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    /*
    异步无返回
     */
    @Async
    public void asyncFun() {
        try {
            Thread.sleep(2000);
            System.out.println("completed!");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    /*
    异步返回
     */
    public CompletableFuture<String> asyncFunReturn() {
        return CompletableFuture.supplyAsync(() ->
        {
            try {
                Thread.sleep(2000);
                return String.valueOf(Thread.currentThread().getId());
            } catch (Exception ex) {
                return "0";
            }
        });
    }

    /*
    同步执行
     */
    public String syncFun() {
        try {
            Thread.sleep(2000);
            String threadId = String.valueOf(Thread.currentThread().getId());
            return threadId;
        } catch (Exception ex) {
            return "0";
        }
    }


}
