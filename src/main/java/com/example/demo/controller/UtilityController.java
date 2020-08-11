package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.model.pojo.Student;

@RestController
@RequestMapping("/utility")
public class UtilityController {

    //region  切面
    /*
     切面类：com.example.demo.aspect.LogAspect
     */
    @PostMapping("/addStudent")
    public void addStudentAspect(@RequestBody Student student) {

        System.out.println("addStudentAspect");
    }
    //endregion

    //region  调度
    /*
     代码路径：demo-->Scheduler
     com.example.demo.Scheduler.TestJob
     */
    //endregion

    //region
    //endregion

    //region
    //endregion
}
