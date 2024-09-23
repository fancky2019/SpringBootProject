package com.example.demo.service.demo;


import com.example.demo.dao.demo.DemoProductMapper;
import com.example.demo.dao.demo.PersonMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@Service
public class PersonService implements IPersonService {

    private static Logger logger = LogManager.getLogger(PersonService.class);


    // JdbcTransactionManager 继承DataSourceTransactionManager 实现接口 PlatformTransactionManager
//    @Autowired
//    private DataSourceTransactionManager transactionManager;

    /**
     *注意配置DataSourceConfig 需要配置PlatformTransactionManager的实现类，之前配置过多数据源的PlatformTransactionManager
     */
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DemoProductMapper demoProductMapper;

    private PersonMapper personMapper;

    private DemoProductService demoProductService;

    private  List<String> list = null;

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


    //region 事务失效场景
    /** 事务失效场景
     * 1. 事务方法非public修饰:
     * 2. 非事务方法调用事务方法 ：没有加T@ransactional 的方法this 调用事务方法
     * 3. 事务方法的异常被捕获了
     * 4. 事务异常类型不对:不是runtimeException
     * 5.事务传播行为不对:事务传播设置了Propagation.REQUIRES_NEW
     * 6. bean没被Spring管理，比如没加Component注解
     * 7. 数据库不支持（比如MyISAM）
     *
     *
     *
     */
    //endregion


    /**
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
        IPersonService personService = null;

        if (proxyObj instanceof IPersonService) {
            personService = (IPersonService) proxyObj;
        }
        int i = personMapper.insert(person);
        demoProductService.insertTransactional();

        Person person77 = personMapper.selectByPrimaryKey(77L);
        person77.setName("person77");
        personMapper.updateByPrimaryKey(person77);
        proTest();
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


//        int n = Integer.parseInt("ds");
        return 0;
    }

    /**
     * 测试事务传播，事务未提交能否查询到.
     * 外层事务回滚不影响REQUIRES_NEW 的内层事务提交
     */
    @Transactional(rollbackFor = Exception.class)
    public void proTest() {

        String name = "fancky75";
        Person person = new Person();
        person.setName(name);
        person.setAge(27);
        person.setBirthday(LocalDateTime.now());
        int i = personMapper.insert(person);
        person.setAge(28);
////        代理对象调用
//        Object proxyObj = AopContext.currentProxy();
//        IPersonService personService = null;
//
//        if (proxyObj instanceof IPersonService) {
//            personService = (IPersonService) proxyObj;
//            personService.insertUnCommit(person);
//            personService.insertUnCommit(person);
//            //可以查询到事务插入的事务（尽管传播是REQUIRES_NEW 和default）
//            List<Person> p = personMapper.selectByName(name);
//            int n = 0;
//            int m = Integer.parseInt("m");
//        }
//        Object proxyObj = AopContext.currentProxy();
//        IPersonService personService = null;
//
//        if (proxyObj instanceof IPersonService) {
//            personService = (IPersonService) proxyObj;
//            personService.insertUnCommit(person);
//        }
        //非事务方法直接调用事务方法，非事务方法异常，事务不会回滚
        this.insertUnCommit(person);
        List<Person> p = personMapper.selectByName(name);

        //查询其他事务方法插入的：可以查询到
        List<Person> list = personMapper.selectByName("fancky8888");

        //查询其他事务方法修改的：可以查询到其他事务修改的数据
        Person person77 = personMapper.selectByPrimaryKey(77L);
        int n = 0;
        int m = Integer.parseInt("m");

    }

    @Transactional(rollbackFor = Exception.class)
//    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public int insertUnCommit(Person person) {
        int i = personMapper.insert(person);
//        int n = 0;
//        int m = Integer.parseInt("m");
        return 0;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int insertB() {


        demoProductService.insertTransactional();

//        int n = Integer.parseInt("ds");
        return 0;
    }

    //如果不模拟此事务传播，可设置 Propagation.REQUIRES_NEW
//    @Transactional(rollbackFor = Exception.class)
    public void transactionalSynchronizedTest(Integer i) throws InterruptedException {

//        CompletableFuture.runAsync(() ->
//        {
//            try {
//                for (int i = 0; i < 100; i++) {
//                    update();
//                }
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        CompletableFuture.runAsync(() ->
//        {
//            try {
//
//                for (int i = 0; i < 100; i++) {
//                    update();
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });

//        for (int i = 0; i < 100; i++) {
//        CompletableFuture.runAsync(() ->
//        {
//            try {
//
//                    update();
//
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        }


//        //模拟两个线程并发造成事务不安全。前段postman 请求两次
//        try {
//
////前段两次请求，一个线程的耗时事务调用update和另一个线程不耗时的任务分别调用update，出现并发问题，没有加2
//
//        问题核心原因：事务传播，长事务，造成事务未能提交，事务之间数据不可见，二类更新丢失
////            解决办法：1、更新时候添加版本号，判断更新成功失败
////                    2、让事务尽快提交，避免和其他事务在一起由于事务传播而未提交
//                       3、TransactionAspectSupport可控制事务的回滚
// //                       参见com.example.demo.service.wms.OrderManagerServicePlatformTransactionManager
//                      4、手动控制事务的提交，不使用Transactional注解，使用编程性事务PlatformTransactionManager接口
//            update();
//            if (i == 0) {
//
//                Thread.sleep(5000);
//
//            }
//
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


        updateWrapper(i);
    }

    /**
     * jdbc  con.setAutoCommit(false);   con.commit();   con.rollback();
     *
     *
     */
    @Override
    public void manualCommitTransaction() {
        //TransactionStatus transaction = transactionManager.getTransaction(TransactionDefinition.withDefaults());
//        DataSourceTransactionManager 实现接口 PlatformTransactionManager.定义了事务的提交，回滚
       //DefaultTransactionDefinition实现接口TransactionDefinition。设置隔离、传播、超时、只读
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
//        definition.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        // 设置事务传播行为
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //事务状态，还原点
        TransactionStatus transaction = transactionManager.getTransaction(definition);

        try {
            List<DemoProduct> list = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                DemoProduct demoProduct = new DemoProduct();
                String uuid = UUID.randomUUID().toString();
//            uuid = "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
//                    + "dssssssssssssssssssssssssssssssssssssssssss";
                demoProduct.setGuid(uuid);
                demoProduct.setProductName("productNameshish事务" + i);
                demoProduct.setProductStyle("productStyle" + i);
                demoProduct.setImagePath("D:\\fancky\\git\\Doc");
                demoProduct.setCreateTime(LocalDateTime.now());
                demoProduct.setModifyTime(LocalDateTime.now());
                demoProduct.setStatus(Short.valueOf("1"));
                demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
                demoProduct.setTimestamp(LocalDateTime.now());
                list.add(demoProduct);
            }
//执行dml 同步操作，失败不会继续往下执行
            int i = demoProductMapper.batchInsert(list);

            //模拟异常
//            int sum = 1 / 0;

            transactionManager.commit(transaction);
        }catch (Exception e){
            log.info(e.getMessage());
            //手动控制回滚异常
            transactionManager.rollback(transaction);
           throw  e;
        }
    }


    public synchronized void updateWrapper(int i) throws InterruptedException {
        update();
        if (i == 0) {

            Thread.sleep(5000);

        }
    }


    @Transactional(rollbackFor = Exception.class)
    public synchronized void update() throws InterruptedException {

        Person person = personMapper.selectByPrimaryKey(2L);
        person.setAge(person.getAge() + 1);
        int i = personMapper.updateByPrimaryKey(person);
//        Thread.sleep(10);

    }

}
