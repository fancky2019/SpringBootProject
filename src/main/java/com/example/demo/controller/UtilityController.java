package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.EnumParamPojo;
import com.example.demo.model.pojo.UnitEnum;
import com.example.demo.model.viewModel.MessageResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.pojo.Student;

import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.util.HashMap;

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
     * 注意：当@RequestBody做参数，前台参数首字母小写
     * 前端： Headers:Content-type-->application/json ,body:raw; 后端：@RequestBody
     * <p>
     * body:raw 内容 {
     * "id":2678045,
     * "relativestate":true,
     * "eosuserinfo":"sdsdsdsdsd"
     * }
     *
     * @param user
     * @return
     */
    @PostMapping("/updateRelative")
    public MessageResult<Integer> updateRelative(@RequestBody Users user) {

        return null;
    }

    /**
     * Headers:Content-type-->multipart/form-data; boundary=<calculated when request is sent>
     * body: formdata:设置 key--value
     *
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

    //region 枚举测试

    /**
     * Jackson对枚举进行序列化,默认输出枚举的String名称。名字要对应，区分大小写。如:Zhi
     * 前端传枚举成员名称（注：不能加双引号）给枚举字段。
     */
    @RequestMapping("/enumParamTest")
    public EnumParamPojo enumParamTest(EnumParamPojo pojo) {

        String zhiStr = UnitEnum.Zhi.toString();//Zhi
        String tou = UnitEnum.Tou.toString();//TOU
        return pojo;
    }
    //endregion

    //region GlobalExceptionHandler
    @GetMapping(value = "/globalExceptionHandlerTest")
    public String globalExceptionHandlerTest() {
        Integer m = Integer.parseInt("m");
        return "error11111";
    }

    /**
     * 有异常支持抛出，让ExceptionHandler处理
     */
    @GetMapping(value = "/globalExceptionHandlerTest1")
    public String globalExceptionHandlerTest1() throws Exception {
        throw new Exception("controller throw");
      //  return "error11111";
    }
    //endregion
}
