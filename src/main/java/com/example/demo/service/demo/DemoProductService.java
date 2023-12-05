package com.example.demo.service.demo;

import com.example.demo.dao.demo.DemoProductMapper;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.pojo.PageData;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.utility.MqSendUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class DemoProductService {

    private static final Logger logger = LogManager.getLogger(DemoProductService.class);
    @Autowired
    DemoProductMapper demoProductMapper;
    @Autowired
    IMqMessageService mqMessageService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private RabbitMQTest rabbitMQTest;

    @Autowired
    private MqSendUtil mqSendUtil;

    public void test() {
//        batchInsert();
//        this.getMaxId();
//        this.getById();
//        this.getByIds();
//        mutiThread();
//        spring 事务基于对象aop 代理实现的 ，不能在方法内调用，否则事务失效
//        insertTransactional();
        insert();
    }

//    @Autowired
//    private PersonService personService;

    public int insert() {

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
        logger.info(stopWatch.shortSummary());

        return 0;
    }

    /*
   数据量过大，分批量插入：5K每次；1W条耗时1.3s左右
  */
    public int batchInsert() {
        List<DemoProduct> list = new ArrayList<>();
        String productName = "productName";
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
        logger.info(stopWatch.shortSummary());

        return 0;
    }

    public int batchInsertSession() {
        List<DemoProduct> list = new ArrayList<>();
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
        while (true) {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        StopWatch stopWatch = new StopWatch("BatchInsert");
//        stopWatch.start("BatchInsert_Trace1");
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
//        //不能用spring 注入的mapper,必须从session 里取，否则是一条一条插入
//        DemoProductMapper mapper = sqlSession.getMapper(DemoProductMapper.class);
//        list.forEach(mapper::insert);
////        sqlSession.clearCache();
//        sqlSession.commit();
//
//        stopWatch.stop();
////        stopWatch.start("BatchInsert_Trace2");
//        long miils = stopWatch.getTotalTimeMillis();
//        logger.info(stopWatch.shortSummary());

//        return 0;
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
            logger.info("会话插入时间：" + miils + "ms   ," + stopWatch.shortSummary());
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
        logger.info(stopWatch.shortSummary());

        return 0;
    }

    public int batchUpdate() {
        List<DemoProduct> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setId(new BigInteger((i + 1) + ""));
            demoProduct.setGuid(UUID.randomUUID().toString());
            demoProduct.setProductName("productName" + (i + 1));
            demoProduct.setProductStyle("productStyle" + (i + 1));
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

        this.demoProductMapper.batchUpdate(list);

        stopWatch.stop();
//        stopWatch.start("BatchInsert_Trace2");
        long miils = stopWatch.getTotalTimeMillis();
        logger.info(stopWatch.shortSummary());

        return 0;
    }

    /*
    spring 事务基于对象aop 代理实现的 ，不能在方法内调用，否则事务失效
     */

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int insertTransactional() {
        List<DemoProduct> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            DemoProduct demoProduct = new DemoProduct();
            demoProduct.setGuid(UUID.randomUUID().toString());
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

        int i = demoProductMapper.batchInsert(list);

        String msgId = UUID.randomUUID().toString();
        String msgContent = "setMsgContent";

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

        mqSendUtil.send(mqMessage);
        return 0;
    }

    public DemoProduct selectByPrimaryKey(Integer id) {
        return this.demoProductMapper.selectByPrimaryKey(id);
    }


    public PageData<DemoProduct> getPageData(DemoProductRequest request) {
        PageData<DemoProduct> pageData = new PageData<>();
        List<DemoProduct> list = demoProductMapper.getPageData(request);
        pageData.setRows(list);
        pageData.setCount(1);
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
            pageData.setRows(pageInfo.getList());
            pageData.setCount(pageInfo.getSize());
        } finally {
            PageHelper.clearPage(); //清理 ThreadLocal 存储的分页参数,保证线程安全
        }
        return pageData;
    }


    void getMaxId() {
        BigInteger result = this.demoProductMapper.getMaxId();
        int m = 0;
    }

    void getById() {
        ProductTest result = this.demoProductMapper.getById(new BigInteger("1"));
        int m = 0;
    }

    void getByIds() {
        List<BigInteger> ids = new ArrayList<>();
        ids.add(new BigInteger("1"));
        ids.add(new BigInteger("2"));
        List<ProductTest> result = this.demoProductMapper.getByIds(ids);
        int m = 0;
    }
}
