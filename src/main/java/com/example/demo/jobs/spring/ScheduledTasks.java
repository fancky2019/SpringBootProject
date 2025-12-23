package com.example.demo.jobs.spring;

import com.example.demo.service.demo.IProductTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ScheduledTasks {

    //    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    @Autowired
    private IProductTestService productTestService;
//
//    @Autowired
//    private BasicInfoCacheService basicInfoCacheService;
//
//    @Autowired
//    private MqMessageService mqMessageService;

//    // 每5秒执行一次
//    @Scheduled(fixedRate = 5000)
//    public void taskWithFixedRate() {
//        System.out.println("固定频率任务 - " + System.currentTimeMillis());
//    }

//    // 上次任务结束后延迟3秒执行
//    @Scheduled(fixedDelay = 3000)
//    public void taskWithFixedDelay() {
//        System.out.println("固定延迟任务 - " + System.currentTimeMillis());
//    }

//    /**
//     * 三点
//     * @throws InterruptedException
//     */
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void initInventoryInfoFromDb() throws InterruptedException {
//        try {
//            log.info("ScheduledTasks initInventoryInfoFromDb");
//            inventoryInfoService.initInventoryInfoFromDb();
//        } catch (Exception ex) {
//            log.error("", ex);
//        }
//
//    }
//
//    /**
//     * 两点
//     * @throws InterruptedException
//     */
//    @Scheduled(cron = "0 0 2 * * ?")
//    public void initBasicInfoCache() throws InterruptedException {
//        try {
//            log.info("ScheduledTasks initBasicInfoCache");
//            basicInfoCacheService.initBasicInfoCache();
//        } catch (Exception ex) {
//            log.error("", ex);
//        }
//
//    }

    /**
     * 5分钟一次
     * @throws Exception
     */
    @Scheduled(cron = "*/10 * * * * ?")  //10s 一次
//    @Scheduled(cron = "0 */5 * * * ?") //5min 一次
    public void beanMethodJobHandler() throws Exception {

        try {

            log.info("ScheduledTasks productTestService");
            //定时任务内不要使用AopContext.currentProxy()，使用注册自身
            boolean proxy = AopUtils.isAopProxy(productTestService);

            // 1. 检查代理类型
            boolean isAopProxy = AopUtils.isAopProxy(productTestService);
            boolean isCglibProxy = AopUtils.isCglibProxy(productTestService);
            boolean isJdkProxy = AopUtils.isJdkDynamicProxy(productTestService);

            //spring 定时任务调用事务方法也可以开启事务
//                        productTestService.transactionalFun();
//            CompletableFuture.runAsync(() ->
//            {
//                productTestService.transactionalFun();
//            });
//            productTestService.transactionalFunAsyncWrapper();

            productTestService.noTranMethodCallTranMethod();

        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}