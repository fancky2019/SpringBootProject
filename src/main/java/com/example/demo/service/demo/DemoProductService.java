package com.example.demo.service.demo;

import com.example.demo.dao.demo.DemoProductMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.utility.MqSendUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 新demo  参见 demo-->  com.example.demo.service.demo.impl;
 */
@Service
@Slf4j
public class DemoProductService {

    //    private static final Logger logger = LogManager.getLogger(DemoProductService.class);
    @Autowired
    DemoProductMapper demoProductMapper;
    @Autowired
    IMqMessageService mqMessageService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private MqSendUtil mqSendUtil;
    @Autowired
    private RabbitMQTest rabbitMQTest;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public MessageResult<Void> test() throws Exception {
//        batchInsert();
//        this.getMaxId();
//        this.getById();
//        this.getByIds();
//        mutiThread();
//        spring 事务基于对象aop 代理实现的 ，不能在方法内调用，否则事务失效
//        insertTransactional();
        // throw  new Exception("controller exception test");
        insert();
        return MessageResult.success();
    }

//    @Autowired
//    private PersonService personService;

    @Transactional(rollbackFor = Exception.class)
    public MessageResult<Void> insert() {

        String productName = "productName";

        DemoProduct demoProduct = new DemoProduct();
        demoProduct.setGuid(UUID.randomUUID().toString());
        demoProduct.setProductName(productName);
        demoProduct.setProductStyle("productStyle");
        demoProduct.setImagePath("D:\\fancky\\git\\Doc");
        demoProduct.setCreateTime(LocalDateTime.now());
        demoProduct.setModifyTime(LocalDateTime.now());
        demoProduct.setStatus(Short.valueOf("1"));
        demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
        demoProduct.setTimestamp(LocalDateTime.now());


        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        int i = demoProductMapper.insert(demoProduct);
        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

        return MessageResult.success();
    }

    /*
   数据量过大，分批量插入：5K每次；1W条耗时1.3s左右
  */
    public int batchInsert() {
        List<DemoProduct> list = new ArrayList<>();
        String productName = "productName";
        // Packet for query is too large (80,087,984 > 67,108,864).
        //max_allowed_packet默認64M
        for (int i = 0; i < 100000; i++) {

            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setGuid(UUID.randomUUID().toString());
            demoProduct.setProductName(productName + i);
            demoProduct.setProductStyle("productStyle" + i);
            demoProduct.setImagePath("D:\\fancky\\git\\Doc");
            demoProduct.setCreateTime(LocalDateTime.now());
            demoProduct.setModifyTime(LocalDateTime.now());
            demoProduct.setStatus(Short.valueOf("1"));
            demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
            demoProduct.setTimestamp(LocalDateTime.now());
            list.add(demoProduct);
        }

        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        int i = demoProductMapper.batchInsert(list);
        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

        return 0;
    }


    public int batchInsertSession() {
        StopWatch stopWatch = new StopWatch("BatchInsert1");
        stopWatch.start("BatchInsert_builder");
        List<DemoProduct> list = new ArrayList<>();
//        300000
        for (int i = 0; i < 300000; i++) {
            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setGuid(UUID.randomUUID().toString());
            demoProduct.setProductName("productName" + i);
            demoProduct.setProductStyle("productStyle" + i);
            demoProduct.setImagePath("D:\\fancky\\git\\Doc");
            demoProduct.setCreateTime(LocalDateTime.now());
            demoProduct.setModifyTime(LocalDateTime.now());
            demoProduct.setStatus(Short.valueOf("1"));
            demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
            demoProduct.setTimestamp(LocalDateTime.now());
            list.add(demoProduct);
        }
        stopWatch.stop();
//        while (true) {
//            try {
//                Thread.sleep(60 * 1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        log.info("BatchInsert_builder cost " +  stopWatch.getTotalTimeMillis() + "ms - " + stopWatch.shortSummary());
        stopWatch = new StopWatch("BatchInsert2");
        stopWatch.start("BatchInsert_insert");

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);) {
            //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
            DemoProductMapper mapper = sqlSession.getMapper(DemoProductMapper.class);
            list.forEach(mapper::insert);
//        sqlSession.clearCache();
            sqlSession.commit();
        }
//        catch (Exception ex)
//        {
//            throw ex;
//        }

        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info("BatchInsert_insert cost " + miils + "ms - " + stopWatch.shortSummary());

        return 0;
    }

    /**
     * 多线程批量插入性能优于单线程批量插入
     * 200W数据 一3分钟
     */
    public void mutiThread() {
        try {
            int batchCount = 20;
            int batchSize = 100000;
            List<List<DemoProduct>> total = new ArrayList<>();

            for (int i = 0; i < batchCount; i++) {
                List<DemoProduct> list = new ArrayList<>();
                total.add(list);
            }
            String productName = "productName";

            int totalCount = batchSize * batchCount;
            for (int i = 0; i < totalCount; i++) {
                if (i > totalCount / 2) {
                    productName = "产品名称";
                }
                DemoProduct demoProduct = new DemoProduct();
                demoProduct.setGuid(UUID.randomUUID().toString());
                demoProduct.setProductName(productName + i);
                demoProduct.setProductStyle("productStyle" + i);
                demoProduct.setImagePath("D:\\fancky\\git\\Doc");
                demoProduct.setCreateTime(LocalDateTime.now());
                demoProduct.setModifyTime(LocalDateTime.now());
                demoProduct.setStatus(Short.valueOf("1"));
                demoProduct.setDescription("setDescription_sdsdddddddddddddddd");
                demoProduct.setTimestamp(LocalDateTime.now());
                int j = i / batchSize;
                List<DemoProduct> list = total.get(j);
                list.add(demoProduct);

            }

            StopWatch stopWatch = new StopWatch("MutiThreadBatchInsert");
            stopWatch.start("MutiThreadBatchInsert_Trace1");

//            for (int i = 0; i < batchCount; i++) {
//                List<DemoProduct> list = total.get(i);
//                batchInsert(list);
//            }


//            List<CompletableFuture<Void>> futures = new ArrayList<>();
//            for (int i = 0; i < 20; i++) {
//                List<DemoProduct> list = total.get(i);
//                CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
//                {
//                    batchInsert(list);
//                });
//                futures.add(future);
//            }
//            CompletableFuture<Void>[] completableFutures = new CompletableFuture[20];
//            futures.toArray(completableFutures);
//            CompletableFuture.allOf(completableFutures);


            final CountDownLatch latch = new CountDownLatch(batchCount);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60000,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000));

            for (int i = 0; i < batchCount; i++) {
                List<DemoProduct> list = total.get(i);
                executor.execute(() ->
                {
                    batchInsert(list);
                    latch.countDown();//当前线程调用此方法，则计数减一
                });
            }
            latch.await();//阻塞当前线程，直到计数器的值为0

            stopWatch.stop();
            long miils = stopWatch.getTotalTimeMillis();
            log.info("会话插入时间：" + miils + "ms   ," + stopWatch.shortSummary());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void batchInsert(List<DemoProduct> list) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
        DemoProductMapper mapper = sqlSession.getMapper(DemoProductMapper.class);
        list.forEach(mapper::insert);
//        sqlSession.clearCache();
        sqlSession.commit();
    }

    public int batchDelete() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {

            list.add(i);
        }

        StopWatch stopWatch = new StopWatch("BatchInsert");
        stopWatch.start("BatchInsert_Trace1");

        this.demoProductMapper.batchDelete(list);
        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info(stopWatch.shortSummary());

        return 0;
    }

    /**
     * 更新18ms。整个方法执行500ms.
     * 内部执行多个 update demo_product SET product_name = '1batchUpdate948', product_style = 'productStyle1200002'  where 1=1  and id = 949
     * @return
     */
    public int batchUpdate() {

        List<BigInteger> ids = new ArrayList<>();

        for (int i = 1; i < 1000; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        List<ProductTest> list = this.demoProductMapper.getByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            ProductTest productTest = list.get(i);
            productTest.setProductName("1batchUpdate" + i);
        }


        StopWatch stopWatch = new StopWatch("batchUpdateCostTime");
        stopWatch.start("batchUpdateCostTime_Trace1");

//        因为mybatis返回的默认是匹配的行数，而不是受影响的行数，如何设置返回的是受影响的行数，useAffectedRows=true
        //mysql 连接字段穿添加  &useAffectedRows=true 返回0 ，不加返回1。
        //for循环多个update 语句以分号结束，update 执行会返回1.因为执行update id 就一条
        //批量更新还是要加锁，避免并发访问
        int result = this.demoProductMapper.batchUpdateProductTest(list);

        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        log.info("batchUpdateCostTime - {}", stopWatch.shortSummary());

        return 0;
    }

    /*
    spring 事务基于对象aop 代理实现的 ，不能在方法内调用，否则事务失效
    事务内不能try catch ，不然要手动抛出异常，否则内部吞噬异常，导致事务失效
     */
//    @Transactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public int insertTransactional() {
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

//        int n = Integer.parseInt("ds");


        String msgId = UUID.randomUUID().toString();
        String msgContent = "setMsgContent";
        //rabbitMq 发送消息线程和spring事务不在同一个线程内，mq 内部抛出异常无法被spring 事务捕获，spring 无法事务回滚
        MqMessage mqMessage = new MqMessage
                (RabbitMQConfig.BATCH_DIRECT_EXCHANGE_NAME,
                        RabbitMQConfig.BATCH_DIRECT_ROUTING_KEY,
                        RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME,
                        msgContent);


        mqMessageService.save(mqMessage);
//        int m = Integer.parseInt("m");


        //处理事务回调发送信息到mq
//        boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
//        // 判断当前是否存在事务,，如果没有开启事务是会报错的
//        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
//            // 无事务，异步发送消息给kafk
//            CompletableFuture.runAsync(() -> {
//                // 发送消息给kafka
//                try {
//                    // 发送消息给kafka
//                } catch (Exception e) {
//                    // 记录异常信息，发邮件或者进入待处理列表，让开发人员感知异常
//                }
//            });
//            return 0;
//        }

        //1、在一个事务内将消息连同业务信息一同写入消息表


        // 有事务，则添加一个事务同步器，并重写afterCompletion方法（此方法在事务提交后会做回调）
// 如果开始了事务则在这里注册一个同步事务，将监听当前线程事务的动作
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//            @Override
//            public void afterCompletion(int status) {
//                //afterCommit
//                // 调用父类的事务提交方法,空方法
////                super.afterCompletion(status);
//                CompletableFuture.runAsync(() -> {
//                    // 发送消息给kafka
//                    try {
//
//                      rabbitMQTest.test(mqMessage);
//                        // 发送消息给kafka
//                    } catch (Exception e) {
//
//                        // 记录异常信息，发邮件或者进入待处理列表，让开发人员感知异常
//                    }
//                });
//
//            }
//        });

        //事务回调：事务同步，此处待处理，
//        TransactionSynchronizationManager.registerSynchronization(
//                new TransactionSynchronizationAdapter() {
//                    @Override
//                    public void afterCommit() {
//                        CompletableFuture.runAsync(() -> {
//                            // 发送消息给kafka
//                            try {
//                                rabbitMQTest.test();
//                                // 发送消息给kafka
//                            } catch (Exception e) {
//
//                                // 记录异常信息，发邮件或者进入待处理列表，让开发人员感知异常
//                            }
//                        });
//                    }
//                });


        //mybatis 操作数据库异常会抛出异常，mybatis貌似单线程执行。代码不会执行到发送mq
        mqSendUtil.send(mqMessage);

        //事务机制和 confirm 机制，事务机制是同步的， confirm 机制是异步的
        //建立连接是同步
        //rabbitmq 内部默认异步发送，不能保证一定发送成功。才会有setConfirmCallback 确认消息发送到交换机
//        rabbitMQTest.produceTest(mqMessage);
        //mq 内部执行失败此时事务已提交，就造成不是原子性

//        int m=11;
        return 0;
    }

    public DemoProduct selectByPrimaryKey(Integer id) {
//        ApplicationContext applicationContext = SpringApplicationContextHelper.getApplicationContext();
//        DemoProductMapper mapper= (DemoProductMapper)applicationContext.getBean(DemoProductMapper.class.getName());
        return this.demoProductMapper.selectByPrimaryKey(id);

//        return mapper.selectByPrimaryKey(id);
    }


    public PageData<DemoProduct> getPageData(DemoProductRequest request) {
        PageData<DemoProduct> pageData = new PageData<>();
        List<DemoProduct> list = demoProductMapper.getPageData(request);
        pageData.setData(list);
        pageData.setCount(1L);
        return pageData;
    }

    public PageData<DemoProduct> pageHelper(DemoProductRequest request) {
        PageData<DemoProduct> pageData = new PageData<>();
        PageHelper.startPage(request.getPageIndex(), request.getPageSize());
        try {
            //拦截要执行的sql:先执行 select count(0),然后执行select 列
            //简单的sql  count(0) 能替换列，复杂的sql 直接包了一层sql 然后count(0)
            List<DemoProduct> list = demoProductMapper.query(request);
            //调用分页查询的方法
            PageInfo<DemoProduct> pageInfo = new PageInfo<>(list);
            pageData.setData(pageInfo.getList());
            pageData.setCount(Long.valueOf(pageInfo.getSize()));
        } finally {
            PageHelper.clearPage(); //清理 ThreadLocal 存储的分页参数,保证线程安全
        }
        return pageData;
    }


    void getMaxId() {
        BigInteger result = this.demoProductMapper.getMaxId();
        int m = 0;
    }

    /**
     * 平均 7ms 左右 ，偶尔1ms
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageResult<ProductTest> getById() {
        Random random = ThreadLocalRandom.current();
        //ID 要切换 ，spring 有缓存机制
        int id = random.nextInt(99);
        StopWatch stopWatch = new StopWatch("");
        stopWatch.start("");
        ProductTest result = this.demoProductMapper.getById(new BigInteger(id + ""));
        stopWatch.stop();
        //7ms 左右
        long costTime = stopWatch.getTotalTimeMillis();
        log.info("getByIdTrace - {} cost_time {} ms  ", id, costTime);

        return MessageResult.success(result);
    }

    void getByIds() {
        List<BigInteger> ids = new ArrayList<>();
        ids.add(new BigInteger("1"));
        ids.add(new BigInteger("2"));
        List<ProductTest> result = this.demoProductMapper.getByIds(ids);
        int m = 0;
    }


    //region 事务测试
    // UtilityController propagation 方法
    //endregion


    //region 初始化基础数据
    @Async("threadPoolExecutor")
    public void initRedis() throws JsonProcessingException {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        log.info("threadId - {}", Thread.currentThread().getId());
        MessageResult<ProductTest> result = this.getById();
        ProductTest productTest = result.getData();
        String jsonStr = objectMapper.writeValueAsString(productTest);
        operations.set("ProductTest:" + productTest.getId(), jsonStr);
        log.info("-----------------初始化基础数据完成---------------------");
    }
    //endregion

}
