package com.example.demo.quartz;

import com.example.demo.controller.JdbcTemplateController;
import com.example.demo.model.pojo.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * '
 * 或者直接实现Job接口
 */

@DisallowConcurrentExecution
public class UpdateJob extends QuartzJobBean {
    private static Logger logger = LogManager.getLogger(UpdateJob.class);
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info(MessageFormat.format("{0} - 开始执行任务。", LocalDateTime.now().format(dateTimeFormatter)));
        String batchId = context.getJobDetail().getKey().getName();
        JobDetail jd = context.getJobDetail();
        JobDataMap jobDataMap = jd.getJobDataMap();
        String[] keys = jobDataMap.getKeys();

        Object obj = jobDataMap.get(keys[0]);
        Student student= (Student) obj;
        System.out.println(MessageFormat.format("执行的任务id为：[{0}] - {1}", batchId,student.getName()));
    }

}
