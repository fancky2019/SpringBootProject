package com.example.demo.Scheduler;

import com.example.demo.model.pojo.Student;

import java.text.MessageFormat;

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
