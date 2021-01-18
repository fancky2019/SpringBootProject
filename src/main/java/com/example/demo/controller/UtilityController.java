package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.pojo.Student;

import java.math.BigDecimal;

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

    /**
     *  前端： Headers:Content-type-->application/json ,body:raw; 后端：@RequestBody
     *
     * body:raw 内容 {
     *     "id":2678045,
     *     "relativestate":true,
     *     "eosuserinfo":"sdsdsdsdsd"
     * }
     * @param user
     * @return
     */
    @PostMapping("/updateRelative")
    public MessageResult<Integer> updateRelative(@RequestBody Users user) {

        return null;
    }

    /**
     *  Headers:Content-type-->multipart/form-data; boundary=<calculated when request is sent>
     *  body: formdata:设置 key--value
     * @param id
     * @param eosorder
     * @param eosbalance
     * @param relativestate
     * @return
     */
    @PostMapping("/postTest")
    public MessageResult<Integer> postTest(@RequestParam Integer id,
                                           @RequestParam String eosorder,
                                           @RequestParam BigDecimal eosbalance,
                                           @RequestParam Boolean relativestate) {

        return null;
    }
    //endregion

    //region
    //endregion
}
