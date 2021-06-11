package com.example.demo.Scheduler;

import com.example.demo.model.pojo.Student;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;

@Configuration
public class DynamicSchedulingConfigurer implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

//        Runnable runnable = () ->
//        {
//            System.out.println("111");
//        };

        for(int i=1;i<5;i++) {
            Student student = new Student();
            student.setAge(i);
            student.setName("fancky");

            Runnable runnable = new ScheduleTask(student);
            Trigger trigger = (triggerContext) ->
            {
                String cron = "*/5 * * * * ?";
                CronTrigger cronTrigger = new CronTrigger(cron);
                Date nextExecDate = cronTrigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            };

            scheduledTaskRegistrar.addTriggerTask(runnable, trigger);
        }
    }
}
