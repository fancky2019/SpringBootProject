package com.example.demo.Scheduler;

import com.example.demo.model.pojo.Student;

import java.text.MessageFormat;

/**
 * 容器初始化完成执行：ApplicationRunner-->CommandLineRunner-->ApplicationReadyEvent
 *ApplicationReadyEvent 事件触发-->任务调度器（TaskScheduler）开始工作
 */
public class ScheduleTask implements Runnable {

    Student student;

    public ScheduleTask() {

    }

    public ScheduleTask(Student student) {
        this.student = student;
    }

    @Override
    public void run() {
      System.out.println(MessageFormat.format("姓名：{0},年龄：{1}", student.getName(),student.getAge()));
    }
}
