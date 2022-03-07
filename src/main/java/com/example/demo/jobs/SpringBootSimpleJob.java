package com.example.demo.jobs;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;

@Component
public class SpringBootSimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
//        JobDetail jd = shardingContext.();
//        JobDataMap jobDataMap = jd.getJobDataMap();
//        String[] keys = jobDataMap.getKeys();

        String param = shardingContext.getJobParameter();
        switch (shardingContext.getShardingItem()) {
            case 0:
                // do something by sharding item 0
                System.out.println(0);
                // int a = 1 / 0;
                break;
            case 1:
                // do something by sharding item 1
                System.out.println(1);
                break;
            case 2:
                // do something by sharding item 2
                System.out.println(2);
                break;
            // case n: ...
        }
    }
}
