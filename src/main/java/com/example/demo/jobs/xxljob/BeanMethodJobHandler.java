package com.example.demo.jobs.xxljob;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BeanMethodJobHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeanMethodJobHandler.class);

    @XxlJob("beanMethodJobHandler")
    public void beanMethodJobHandler() throws Exception {
        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();
        // XxlJobLogger.log("bean method jobhandler running...");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        XxlJobHelper.log("BeanMethodJobHandler");
        LOGGER.info("xxljob - BeanMethodJobHandler  {} ", timeStr);

    }

    @XxlJob("dynamicJob")
    public void dynamicJob() throws Exception {
        // XxlJobLogger.log("bean method jobhandler running...");

        //param xxl admin 填写的任务参数
        String param =   XxlJobHelper.getJobParam();
        System.out.println("dynamicJob - "+param );
        XxlJobHelper.log("dynamicJob");
        LOGGER.info("dynamicJob");

    }
}
