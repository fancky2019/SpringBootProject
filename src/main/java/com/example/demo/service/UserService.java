package com.example.demo.service;

import com.example.demo.dao.rabc.UsersMapper;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.entity.wms.Order;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import sun.reflect.generics.tree.VoidDescriptor;

import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;


//@Service("UserService")
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
            long threadId = Thread.currentThread().getId();
            System.out.println(MessageFormat.format("thread - {0} execute!", threadId));
            Thread.sleep(20000);
            System.out.println(MessageFormat.format("thread {0} completed!", threadId));
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


    /*

    springboot开启事务很简单，只需要一个注解@Transactional 就可以了。
    因为在springboot中已经默认对jpa、jdbc、mybatis开启了事事务，
    引入它们依赖的时候，事物就默认开启



     自动回滚：  在@Transactional注解中如果不配置rollbackFor属性,那么事物只会在遇到RuntimeException的时候才会回滚,
    加上rollbackFor=Exception.class,Exception还要抛出。 可以让事物在遇到非运行时异常时也回滚

    手动回滚：TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> transactionTest(Order order) {
        MessageResult<Void> messageResult = new MessageResult<>();

        //自动回滚：
        //以下进行数据库事务操作，不要try..catch，以便让spring自动进行事务回滚。
        // 在controller捕捉异常，判断 messageResult.getSuccess()判断事务是否执行成功，
        //如果发生异常，抛到controller里，则不成功。


        //手动回滚:
        //手动回滚可解决数据库操作没有发生异常的情况，即：可以解决业务方面的异常，可手动控制异常
        //更加灵活。
        return messageResult;
    }
}


