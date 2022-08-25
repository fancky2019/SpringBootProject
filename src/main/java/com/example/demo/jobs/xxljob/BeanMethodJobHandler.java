package com.example.demo.jobs.xxljob;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class BeanMethodJobHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeanMethodJobHandler.class);

    @XxlJob("beanMethodJobHandler")
    public void beanMethodJobHandler(String param) throws Exception {
       // XxlJobLogger.log("bean method jobhandler running...");

        LOGGER.info("BeanMethodJobHandler");

    }
}
