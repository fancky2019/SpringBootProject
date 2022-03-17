package com.example.demo.jobs;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobRegistry;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;
//import sun.reflect.generics.tree.VoidDescriptor;

import javax.annotation.Resource;
import java.util.TimeZone;

@Component
public class ElasticJobFactory {
    @Resource
    private ZookeeperRegistryCenter registryCenter;


//    private static class StaticInnerClass{
//        private static final ElasticJobFactory instance=new ElasticJobFactory();
//    }
//
//    public static ElasticJobFactory getInstance() {
//        return StaticInnerClass.instance;
//    }

    public void setUpSimpleJob(String jobName, String cron) {
        //jobName  不能重复
        new ScheduleJobBootstrap(registryCenter, new NoShardingJob(),
                JobConfiguration.newBuilder(jobName, 1)
                        .cron(cron).shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou").build()).schedule();
    }

    public void updateJob(String jobName, String cron) {
        JobRegistry.getInstance().getJobScheduleController(jobName).rescheduleJob(cron, TimeZone.getDefault().getID());
    }

    public void shutDownJob(String jobName) {
        JobRegistry.getInstance().getJobScheduleController(jobName).shutdown();
    }

//    private static CoordinatorRegistryCenter setUpRegistryCenter() {
//        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING, JOB_NAMESPACE);
//        CoordinatorRegistryCenter result = new ZookeeperRegistryCenter(zkConfig);
//        result.init();
//        return result;
//    }

}
