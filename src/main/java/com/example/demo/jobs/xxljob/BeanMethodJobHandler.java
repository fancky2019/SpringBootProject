package com.example.demo.jobs.xxljob;

import com.example.demo.service.demo.IMqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 将@XxlJob里的注解填入后台的JobHandler*
 *
 * http://localhost:8182/xxl-job-admin/toLogin
 * admin,123456
 * 建议采用方法注册:方法上加注解的方式 @XxlJob("beanMethodJobHandler")
 *
 *1、添加执行器
 * 2、添加任务
 *
 */
@Slf4j
@Component
public class BeanMethodJobHandler {

    // private static final Logger LOGGER = LogManager.getLogger(BeanMethodJobHandler.class);

    @Autowired
    private IMqMessageService mqMessageService;

    //region 模板
    @XxlJob("beanMethodJobHandler")
    public void beanMethodJobHandler() throws Exception {
        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();
        // XxlJobLogger.log("bean method jobhandler running...");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        XxlJobHelper.log("BeanMethodJobHandler");
        log.info("xxljob - BeanMethodJobHandler  {} ", timeStr);

    }

    @XxlJob("dynamicJob")
    public void dynamicJob() throws Exception {
        // XxlJobLogger.log("bean method jobhandler running...");

        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();
        System.out.println("dynamicJob - " + param);
        XxlJobHelper.log("dynamicJob");
        log.info("dynamicJob");

    }


    //endregion


    @XxlJob("mqFailHandler")
    public  void mqFailHandler() throws Exception {
        //param xxl admin 填写的任务参数
        String param = XxlJobHelper.getJobParam();

        mqMessageService.mqOperation();
        // XxlJobLogger.log("bean method jobhandler running...");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        XxlJobHelper.log("mqFailHandler");
        log.info("xxljob - mqFailHandler  {} ", timeStr);
    }


}
