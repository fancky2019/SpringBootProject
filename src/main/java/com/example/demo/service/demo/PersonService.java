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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(Person person) {
//        try {
        //获取当前代理的实例
        PersonService personService = (PersonService) AopContext.currentProxy();
        int i = personMapper.insert(person);
        demoProductService.insert();

//        } catch (Exception e) {
//            throw e;
//        }
        return 0;
    }
}
