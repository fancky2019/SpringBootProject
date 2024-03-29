package com.example.demo.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;


//@Order控制配置类的加载顺序，通过@Order指定执行顺序，值越小，越先执行
@Configuration
@Order(2)
public class ApplicationListenerImp implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private QuartzJobComponent quartzJobComponent;
    private static Logger logger = LogManager.getLogger(ApplicationListenerImp.class);



    /*
    一、Spring boot运行时，会发送以下事件

1. ApplicationStartingEvent

2. ApplicationEnvironmentPreparedEvent：当Environment已经准备好，在context 创建前

3. ApplicationContextInitializedEvent：在ApplicationContext 创建和ApplicationContextInitializer都被调用后，但是bean definition没有被加载前

4. ApplicationPreparedEvent：bean definition已经加载，但是没有refresh

5. ApplicationStartedEvent： context 已经被refresh， 但是application 和command-line 的runner都没有被调用

6. AvailabilityChangeEvent

7. ApplicationReadyEvent： application 和command-line 的runner都被调用后触发

8. AvailabilityChangeEvent

9. ApplicationFailedEvent： 启动失败触发



另外，会在ApplicationPreparedEvent之后和ApplicationStartedEvent之前发送

ContextRefreshedEvent
     */


    /**
     * 启程启动时候初始化任务
     * 初始启动quartz
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            logger.info("启动开始...");
          //  quartzJobComponent.addJob(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

