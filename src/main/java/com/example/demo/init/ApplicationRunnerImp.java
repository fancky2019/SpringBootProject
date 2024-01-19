package com.example.demo.init;

import com.example.demo.service.demo.DemoProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


//容器初始化完成执行：ApplicationRunner-->CommandLineRunner-->ApplicationReadyEvent

//@Order控制配置类的加载顺序，通过@Order指定执行顺序，值越小，越先执行
@Component
@Order(1)
@Slf4j
public class ApplicationRunnerImp implements ApplicationRunner {
    private static Logger LOGGER = LogManager.getLogger(ApplicationRunnerImp.class);
    @Value("${config.configmodel.fist-Name}")
    private String fistName;
    @Autowired
    private DemoProductService demoProductService;
    @Resource
    ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String name=fistName;
        LOGGER.info("ApplicationRunnerImp");
        log.info("threadId - {}",Thread.currentThread().getId());
        demoProductService.initRedis();
        int m=0;
    }
}
