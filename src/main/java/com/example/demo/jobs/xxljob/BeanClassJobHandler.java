package com.example.demo.jobs.xxljob;

import com.example.demo.controller.UtilityController;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
此种要到 XxlJobConfig
注册： XxlJobSpringExecutor.registJobHandler("beanClassJobHandler", new BeanClassJobHandler());

 http://localhost:8182/xxl-job-admin/toLogin
admin,123456
建议采用方法注册



注： 表 xxl_job_lock 的内容为schedule_lock，否则锁不住 源码：select * from xxl_job_lock where lock_name='schedule_lock' for update

 调度中心调用具体执行器通过调度策略：admin有对应策略选择

 调度中心高可用设计：通过nginx 反向代理。但是要确保后天数据库是同一连接
 */
@Component
public class BeanClassJobHandler extends IJobHandler {

    private static final Logger LOGGER = LogManager.getLogger(BeanClassJobHandler.class);

    @Override
    public void execute() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = formatter.format(LocalDateTime.now());
        LOGGER.info("xxljob - BeanClassJobHandler {}",timeStr);

    }
}
