package com.example.demo.jobs.xxljob;

import com.example.demo.controller.UtilityController;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/*
此种要到 XxlJobConfig
注册： XxlJobSpringExecutor.registJobHandler("beanClassJobHandler", new BeanClassJobHandler());

建议采用方法注册
 */
@Component
public class BeanClassJobHandler extends IJobHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeanClassJobHandler.class);

    @Override
    public void execute() throws Exception {
        LOGGER.info("BeanClassJobHandler");

    }
}
