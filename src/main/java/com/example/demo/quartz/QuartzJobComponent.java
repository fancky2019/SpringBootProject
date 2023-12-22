package com.example.demo.quartz;

import com.example.demo.model.pojo.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;








//6位 cron 表达式
//0代表从0分开始，*代表任意字符，／代表递增。
//   表达式中的  /  标识增量  每隔



//0 0 1 * * ?     //一点执行
//     "*/5 * * * * ?''   //5s一次  "0 */5 * * * ?''   //5min一次





@Component
public class QuartzJobComponent {
    private static Logger log = LogManager.getLogger(QuartzJobComponent.class);
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ObjectMapper objectMapper ;
//    @Value("${demo.multiEnvironment}")

    @Value("${cron}")
    private  String cron;
    /**
     * @param
     * @Description: 添加一个定时任务
     */
    public void addJob(int ruleId) {

        try {
            JobDataMap jobDataMap = new JobDataMap();
            Student student = new Student();
            student.setName("fancky");
            String jsonStr = objectMapper.writeValueAsString(student);
            jobDataMap.put(String.valueOf(ruleId), student);

            JobDetail job = JobBuilder.newJob(UpdateJob.class)
                    .withIdentity("jobName_" + ruleId, "jobGroupName_" + ruleId)
                    .setJobData(jobDataMap).build();
            // 表达式调度构建器
//            String cron = "0/5 * * * * ?";
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            // 按新的cronExpression表达式构建一个新的trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("triggerName_" + ruleId, "triggerGroupName_" + ruleId)
                    //.startAt(quartzModel.getStartTime()).endAt(quartzModel.getEndTime())
                    .withSchedule(scheduleBuilder)
                    .build();
            scheduler.scheduleJob(job, trigger);
//                // 不立即启动
//                if (!scheduler.isShutdown()) {
//                    scheduler.start();
//                }
        } catch (Exception e) {
            log.error("Add quartz job error, ruleId = {}", ruleId);
        }


    }

//    /**
//     * @param jobName
//     * @param cron
//     * @Description: 修改一个任务的触发时间(使用默认的任务组名 ， 触发器名 ， 触发器组名)
//     */
//    public void modifyJobTime(String jobName, String cron, Date startDate, Date endDate) {
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, GloabalConstant.QZ_TRIGGER_GROUP_NAME);
//
//        try {
//            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//            if (trigger == null) {
//                return;
//            }
//            String oldTime = trigger.getCronExpression();
//            if (!oldTime.equalsIgnoreCase(cron)) {
//                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
//                // 按新的cronExpression表达式重新构建trigger
//                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
//                        .startAt(startDate)
//                        .endAt(endDate).withSchedule(scheduleBuilder).build();
//                // 按新的trigger重新设置job执行
//                scheduler.rescheduleJob(triggerKey, trigger);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    /**
//     * @param oldJobName ：原任务名
//     * @param jobName
//     * @param jobclass
//     * @param cron
//     * @Description:修改任务，（可以修改任务名，任务类，触发时间） 原理：移除原来的任务，添加新的任务
//     */
//    public void modifyJob(String oldJobName, String jobName, Class jobclass, String cron) {
//        /*
//         * removeJob(oldJobName);
//         * addJob(jobName, jobclass, cron);
//         * System.err.println("修改任务"+oldJobName);
//         */
//        TriggerKey triggerKey = TriggerKey.triggerKey(oldJobName, GloabalConstant.QZ_TRIGGER_GROUP_NAME);
//        JobKey jobKey = JobKey.jobKey(oldJobName, GloabalConstant.QZ_JOB_GROUP_NAME);
//        try {
//            Trigger trigger = (Trigger) scheduler.getTrigger(triggerKey);
//            if (trigger == null) {
//                return;
//            }
//            scheduler.pauseTrigger(triggerKey);// 停止触发器
//            scheduler.unscheduleJob(triggerKey);// 移除触发器
//            scheduler.deleteJob(jobKey);// 删除任务
//            System.err.println("移除任务:" + oldJobName);
//
//            JobDetail job = JobBuilder.newJob(jobclass).withIdentity(jobName,
//                    GloabalConstant.QZ_JOB_GROUP_NAME)
//                    .build();
//            // 表达式调度构建器
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
//            // 按新的cronExpression表达式构建一个新的trigger
//            Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(jobName,
//                    GloabalConstant.QZ_TRIGGER_GROUP_NAME)
//                    .withSchedule(scheduleBuilder).build();
//
//            // 交给scheduler去调度
//            scheduler.scheduleJob(job, newTrigger);
//
//            // 启动
//            if (!scheduler.isShutdown()) {
//                scheduler.start();
//                System.err.println("添加新任务:" + jobName);
//            }
//            System.err.println("修改任务【" + oldJobName + "】为:" + jobName);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }

//    /**
//     * @param triggerName
//     * @param triggerGroupName
//     * @param cron
//     * @Description: 修改一个任务的触发时间
//     */
//    public void modifyJobTime(String triggerName, String triggerGroupName, String cron) {
//        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
//        try {
//            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//            if (trigger == null) {
//                return;
//            }
//            String oldTime = trigger.getCronExpression();
//            if (!oldTime.equalsIgnoreCase(cron)) {
//                // trigger已存在，则更新相应的定时设置
//                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
//                // 按新的cronExpression表达式重新构建trigger
//                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
//                // 按新的trigger重新设置job执行
//                scheduler.resumeTrigger(triggerKey);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    /**
//     * @param jobName
//     * @Description 移除一个任务(使用默认的任务组名 ， 触发器名 ， 触发器组名)
//     */
//    public void removeJob(String jobName) {
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, GloabalConstant.QZ_TRIGGER_GROUP_NAME);
//        JobKey jobKey = JobKey.jobKey(jobName, GloabalConstant.QZ_JOB_GROUP_NAME);
//        try {
//            Trigger trigger = (Trigger) scheduler.getTrigger(triggerKey);
//            if (trigger == null) {
//                return;
//            }
//            scheduler.pauseTrigger(triggerKey);// 停止触发器
//            scheduler.unscheduleJob(triggerKey);// 移除触发器
//            scheduler.deleteJob(jobKey);// 删除任务
//            System.err.println("移除任务:" + jobName);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @Description: 移除一个任务
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeJob(int ruleId) {
        TriggerKey triggerKey = TriggerKey.triggerKey("triggerName_" + ruleId, "triggerGroupName_" + ruleId);
        JobKey jobKey = JobKey.jobKey("jobName_" + ruleId, "jobGroupName_" + ruleId);
        try {
            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            log.error("remove quartz job error, ruleId = {}", ruleId);
        }
    }

//    /**
//     * @param jobName
//     * @Description:暂停一个任务(使用默认组名)
//     */
//    public void pauseJob(String jobName) {
//        JobKey jobKey = JobKey.jobKey(jobName, GloabalConstant.QZ_JOB_GROUP_NAME);
//        try {
//            scheduler.pauseJob(jobKey);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @param jobName
     * @param jobGroupName
     * @Description:暂停一个任务
     */
    public void pauseJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * @param jobName
//     * @Description:恢复一个任务(使用默认组名)
//     */
//    public void resumeJob(String jobName) {
//        JobKey jobKey = JobKey.jobKey(jobName, GloabalConstant.QZ_JOB_GROUP_NAME);
//        try {
//            scheduler.resumeJob(jobKey);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @param jobName
     * @param jobGroupName
     * @Description:恢复一个任务
     */
    public void resumeJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    /**
//     * @param jobName
//     * @Description: 立即运行任务，这里的立即运行，只会运行一次，方便测试时用。
//     */
//    public void triggerJob(String jobName) {
//        JobKey jobKey = JobKey.jobKey(jobName, GloabalConstant.QZ_JOB_GROUP_NAME);
//        try {
//            scheduler.triggerJob(jobKey);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * @param jobName
//     * @param jobGroupName
//     * @Description: 立即运行任务，这里的立即运行，只会运行一次，方便测试时用。
//     */
//    public void triggerJob(String jobName, String jobGroupName) {
//        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
//        try {
//            scheduler.triggerJob(jobKey);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * @param jobName 触发器名
//     * @Description: 获取任务状态
//     */
//    public String getTriggerState(String jobName) {
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, GloabalConstant.QZ_TRIGGER_GROUP_NAME);
//        String name = null;
//        try {
//            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
//            name = triggerState.name();
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//        return name;
//    }

//    /**
//     * @param cron
//     * @Description:获取最近5次执行时间
//     */
//    public List<String> getRecentTriggerTime(String cron) {
//        List<String> list = new ArrayList<String>();
//        try {
//            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
//            cronTriggerImpl.setCronExpression(cron);
//            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 5);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//            for (Date date : dates) {
//                list.add(dateFormat.format(date));
//            }
//        } catch (ParseException e) {
//            log.error("GetRecentTriggerTime error, cron = {}", cron, e);
//        }
//        return list;
//    }

}
