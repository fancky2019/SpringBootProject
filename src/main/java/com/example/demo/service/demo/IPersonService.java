package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.Person;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

public interface IPersonService {
    void test();

    int insert(Person person);
     int insertUnCommit(Person record);
    void transactionalSynchronizedTest(Integer i) throws InterruptedException;
    void transactionalSynchronizedTest1(Integer i) throws InterruptedException;

    void manualCommitTransaction();
    void transactionTemplateTest(boolean executeException);
    void  proTest();


}
