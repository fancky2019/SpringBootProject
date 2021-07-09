package com.example.demo.quartz;

import com.example.demo.model.pojo.Student;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.MessageFormat;

/**
 * '
 * 或者直接实现Job接口
 */

@DisallowConcurrentExecution
public class UpdateJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("begin delwith batch task >>>>>>>>>>>>>>>>>>>>>>>");
        String batchId = context.getJobDetail().getKey().getName();
        JobDetail jd = context.getJobDetail();
        JobDataMap jobDataMap = jd.getJobDataMap();
        String[] keys = jobDataMap.getKeys();

        Object obj = jobDataMap.get(keys[0]);
        Student student= (Student) obj;
        System.out.println(MessageFormat.format("执行的任务id为：[{0}] - {1}", batchId,student.getName()));
    }

}
