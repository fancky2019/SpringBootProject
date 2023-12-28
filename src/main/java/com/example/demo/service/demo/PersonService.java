package com.example.demo.service.demo;


import com.example.demo.dao.demo.PersonMapper;
import com.example.demo.model.entity.demo.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Service
public class PersonService implements IPersonService {

    private static Logger logger = LogManager.getLogger(PersonService.class);

    PersonMapper personMapper;

    DemoProductService demoProductService;

    List<String> list = null;

    @Autowired
    public PersonService(PersonMapper personMapper,
                         DemoProductService demoProductService) {

        this.personMapper = personMapper;
        this.demoProductService = demoProductService;
        list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
    }

    public void test() {
        int size = list.size();
    }

    /*
    @Transactional 访问修饰符可以是 protected
    事务内不能try catch ，不然要手动抛出异常，否则内部吞噬异常，导致事务失效

     Propagation.REQUIRED  Propagation.REQUIRED
     insert 无事务， insertTransactional 有事务 ：insert 异常，insertTransactional 不会回滚
     insert 有事务， insertTransactional 有事务 ：两个方法公用一个事务

     Propagation.REQUIRED Propagation.REQUIRES_NEW
     insert 有事务， insertTransactional 有事务 ：外层不影响内层，内层会影响外层 ，内层异常抛出

     Propagation.REQUIRED  Propagation.NESTED :两者共同成功失败
     */
    @Transactional(rollbackFor = Exception.class)
    public int insert(Person person) {

        //获取当前代理的实例 @EnableAspectJAutoProxy(exposeProxy = true)来暴露AOP的Proxy对象才行，否则会报异常
        //PersonService personService = (PersonService) AopContext.currentProxy();

        //如果不加Transactional 注解，currentProxy 是UtilityController 对象
        //加@Transactional 注解代理对象才是PersonService
        Object proxyObj = AopContext.currentProxy();
        IPersonService personService=null;

        if (proxyObj instanceof IPersonService) {
             personService = (IPersonService) proxyObj;
        }
         demoProductService.insertTransactional();
        int i = personMapper.insert(person);

        //没有走动态代理，相当于和当前事务在同一个事务内
//        this.insertB();

        //会通过事务传播机制
//        if(personService!=null)
//        {
//            personService.insertB();
//        }

        // REQUIRES_NEW： 没有就开启，有了挂起原来的，开启新的事务：调用者在老事务，被调用者在新事物中执行互不影响。
        //Propagation.NESTED: 调用者事务存在调用者事务和被调用者事务分别在两个事务中执行，嵌套事务回滚到回滚点。外层事务回滚整个事务
        //spring 不会处理异常，会把异常继续抛出


        int n = Integer.parseInt("ds");
        return 0;
    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public int insertB() {


        demoProductService.insertTransactional();

//        int n = Integer.parseInt("ds");
        return 0;
    }


}
