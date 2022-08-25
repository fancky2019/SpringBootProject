package com.example.demo.service.demo;


import com.example.demo.dao.demo.PersonMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    private static Logger logger = LogManager.getLogger(PersonService.class);

    @Autowired
    PersonMapper personMapper;

    @Autowired
    DemoProductService demoProductService;

    @Transactional(rollbackFor = Exception.class)
    public int insert(Person person) {

        //获取当前代理的实例
        PersonService personService = (PersonService) AopContext.currentProxy();
        int i = personMapper.insert(person);
        try {

            // REQUIRES_NEW： 没有就开启，有了挂起原来的，开启新的事务：调用者在老事务，被调用者在新事物中执行互不影响。
            //Propagation.NESTED: 调用者事务存在调用者事务和被调用者事务分别在两个事务中执行，嵌套事务回滚到回滚点。外层事务回滚整个事务
            //spring 不会处理异常，会把异常继续抛出
            demoProductService.insert();
            int n = 0;
        } catch (Exception e) {
            String msg = e.getMessage();
//            throw e;
        }
//        int m = Integer.parseInt("m");
        return 0;
    }


}
