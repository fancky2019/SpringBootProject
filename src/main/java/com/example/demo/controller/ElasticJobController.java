package com.example.demo.controller;

import com.example.demo.jobs.ElasticJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
ElasticJob-ui :下载下来用命令解压 tar -zxvf apache-shardingsphere-elasticjob-3.0.1-lite-ui-bin.tar.gz，window解压容易出错。
http://localhost:8088/
userName 和password都是  root


使用：一、Global Setting添加注册中心填写ZK信息，保存之后连接.
     二、Job Operation进行job自定义操作。
 */
@RestController
@RequestMapping("/elasticJob")
public class ElasticJobController {

    @Autowired
    ElasticJobFactory elasticJobFactory;

    @GetMapping("/addJob")
    public void  addJob()
    {
        elasticJobFactory.setUpSimpleJob("noShardingJob","0/5 * * * * ?");
    }

    @GetMapping("/updateJob")
    public void  updateJob()
    {
        elasticJobFactory.updateJob("noShardingJob","0/20 * * * * ?");
    }

    @GetMapping("/shutDownJob")
    public void  shutDownJob()
    {
        elasticJobFactory.shutDownJob("testSimpleJob");
    }

}
