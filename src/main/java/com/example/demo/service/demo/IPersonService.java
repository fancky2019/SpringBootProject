package com.example.demo.service.demo;

import com.example.demo.model.entity.demo.Person;
import org.springframework.transaction.annotation.Transactional;

public interface IPersonService {
    void test();

    int insert(Person person);

}
