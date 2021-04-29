package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.EnumParamPojo;
import com.example.demo.model.pojo.UnitEnum;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.ValidatorVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.java.swing.plaf.motif.MotifRadioButtonMenuItemUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.pojo.Student;

import javax.print.DocFlavor;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/utility")
public class UtilityController {

    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;

    @Autowired
    private ObjectMapper mapper;


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

    //region raw、form-data

    /**
     * 注意：当@RequestBody做参数，前台参数首字母小写
     * 前端： Headers:Content-type-->application/json ,body:raw; 后端：@RequestBody
     * <p>
     * body:raw 内容 {
     * "id":2678045,
     * "relativeState":true,
     * "eosUserInfo":"dddd"
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

    //region 自定义controller全局异常处理 GlobalExceptionHandler

    /**
     * service 不进行异常处理，抛出到controller 让controller处理
     * 事务抛出待研究
     */
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

    //region 校验 Validator Hibernator-Validator
    /**
     *  springboot-start-web 默认集成了Hibernator-Validator
     *  注意：@RequestBody @Validated
     */
    /**
     * @param vo
     * @return
     */
    @PostMapping("/validatorTest")
    public String validatorTest(@RequestBody @Validated ValidatorVo vo) {

        return "success";
    }
    //endregion

    //region 多环境测试

    /**
     * 配置：
     * 1、pom文件添加 profile
     * 2、application。yml 设置profiles.active的值@environment@
     * 注：environment的值要和profile的节点名称保持一致
     * 3、每次更改pom里的environment值之后，注意手动刷新maven。
     *
     * @return
     */
    @GetMapping(value = "/multiEnvironmentTest")
    public String multiEnvironmentTest() {
        return multiEnvironment;
    }

    //endregion

    //region HttpServletRequest url  body

    //region 获取Url信息
    @GetMapping(value = "/resolveUrl")
    public String resolveUrl(HttpServletRequest request) {
        // 访问协议 http
        String agreement = request.getScheme();
        // 访问域名 localhost
        String serverName = request.getServerName();
        // 访问端口号 8081
        int port = request.getServerPort();
        // 访问项目名：server.servlet.context-path :/sbp
        String contextPath = request.getContextPath();
        //获取请求方法: /utility/resolveUrl
        String servletPath = request.getServletPath();

        /// 包含Servlet配置的路径; /sbp/utility/resolveUrl
        String requestUri=  request.getRequestURI();
        //参数名称
        List<String> paramNames = Collections.list(request.getParameterNames());
        HashMap<String, String> paramAndValues = new HashMap<>();
        paramNames.forEach(p ->
        {
            paramAndValues.put(p, request.getParameter(p));
        });
        //读取参数值
        String fan = request.getParameter("name");
        // request.getHeader("token")
        return servletPath;
    }
    //endregion

    //region body 不能重复读取

    /**
     * body 不能重复读取
     */
    @PostMapping(value = "/getPostBody")
    public Student getPostBody(HttpServletRequest request) throws Exception {


        String bodyString = getBodyString(request);
        //“”   request.getReader(); c
        //String re = getBodyString(request);

        // 重复读取流：getInputStream() has already been called for this request
        // String bodyString = getBody(request);
        Student student = mapper.readValue(bodyString, Student.class);


        String str = " sd 1 ";
        boolean isNullOrEmpty = StringUtils.isEmpty(str);
        //C# trim
        String str1 = StringUtils.trimWhitespace(str);
        return student;
    }

    String getBodyString(HttpServletRequest request) throws Exception {
        BufferedReader br = request.getReader();
        StringBuilder sb = new StringBuilder();
        String str = "";
        while ((str = br.readLine()) != null) {
            sb.append(str);

        }
        return sb.toString();
    }

    String getBody(HttpServletRequest request) {
        try {
            ServletInputStream in = request.getInputStream();
            String body;
            body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    //endregion

    //endregion

    //region utils
    @GetMapping(value = "/springUtils")
    public String springUtils(HttpServletRequest request) {
        String str = " sd 1 ";
        boolean isNullOrEmpty = StringUtils.isEmpty(str);
        //C# trim
        String str1 = StringUtils.trimWhitespace(str);

        List<String> listStr = null;
        boolean collectionIsNullEmpty = CollectionUtils.isEmpty(listStr);

        //路由匹配
        /*
        ？匹配一个字符(matches one character)。
        *匹配0个或者多个字符 (matches zero or more characters)。
        ** 匹配url中的0个或多个子目录 (matches zero or more directories in a path)
        {spring:[a-z]+} 匹配满足正则表达式[a-z]+的路径，这些路径赋值给变量"spring" (matches the regexp [a-z]+ as a path variable named "spring"）
         */



        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean isMatch = antPathMatcher.match("*/utility/getPostBody", "http://localhost:8081/sbp/utility/getPostBody");
        isMatch = antPathMatcher.match("*/utility*", "http://localhost:8081/sbp/utility/getPostBody");

        return " ";
    }
    //endregion
}
