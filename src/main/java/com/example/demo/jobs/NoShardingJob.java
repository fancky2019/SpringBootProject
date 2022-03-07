package com.example.demo.jobs;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

/*
https://github.com/apache/shardingsphere-elasticjob/tree/master/examples/
 */
//@Component
public class NoShardingJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("SimpleJob - NoShardingJob");
    }
}
