package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.EnumParamPojo;
import com.example.demo.model.pojo.UnitEnum;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.ValidatorVo;
import com.example.demo.quartz.QuartzJobComponent;
import com.example.demo.service.demo.DemoProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.java.swing.plaf.motif.MotifRadioButtonMenuItemUI;
import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.util.*;
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

/*
@Scope bean作用域
1、singleton：单例模式 默认，在spring IoC容器仅存在一个Bean实例，Bean以单例方式存在，bean作用域范围的默认值。
2、prototype ：原型模式，每次从容器中调用Bean时，都返回一个新的实例，即每次调用getBean()时，相当于执行newXxxBean()。
3、request：每次HTTP请求都会创建一个新的Bean，该作用域仅适用于web的Spring WebApplicationContext环境。
4、session：同一个HTTP Session共享一个Bean，不同Session使用不同的Bean。该作用域仅适用于web的Spring WebApplicationContext
5、global session
global session作用域类似于标准的HTTP Session作用域，不过它仅仅在基于portlet的web应用中才有意义。Portlet规范定义了全局Session的概念，
它被所有构成某个 portlet web应用的各种不同的portlet所共享。在global session作用域中定义的bean被限定于全局portlet Session的生命周期范围内。
如果你在web中使用global session作用域来标识bean，那么web会自动当成session类型来使用。



一个session 可能会有多个request.session有过期时间，有点类似 keep alive,避免每次都创建，池化思想。
<!--在tomcat的web.xml文件中设置HttpSession过期时间。 -->
    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>
<!--tomcat默认30分钟 -->
 */


@RestController
@RequestMapping("/utility")
//@Scope("prototype")
public class UtilityController {

    //Lombox 的注解 @Slf4j 相当于下面语句
    private static final Logger logger = LogManager.getLogger(UtilityController.class);

    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DemoProductService demoProductService;

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
        String requestUri = request.getRequestURI();
        //请求头
        String value = request.getHeader("Content-Type");
        List<String> headerNames = Collections.list(request.getHeaderNames());
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

        //请求头
        String value = request.getHeader("Content-Type");
        List<String> headerNames = Collections.list(request.getHeaderNames());
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
        String strBlan = " ";
        boolean res = org.apache.commons.lang3.StringUtils.isBlank(strBlan); //会trim
        boolean res1 = org.apache.commons.lang3.StringUtils.isNotEmpty(strBlan); //不会trim


        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean isMatch = antPathMatcher.match("*/utility/getPostBody", "http://localhost:8081/sbp/utility/getPostBody");
        isMatch = antPathMatcher.match("*/utility*", "http://localhost:8081/sbp/utility/getPostBody");

        return " ";
    }
    //endregion

    //region log4j2test

    //region 配置邮件

    /*
    参考链接：https://blog.csdn.net/david_pfw/article/details/85846351
    1、
//       <!--        log4j2邮件-->
//        <dependency>
//            <groupId>javax.activation</groupId>
//            <artifactId>activation</artifactId>
//            <version>1.1.1</version>
//        </dependency>
//        <dependency>
//            <groupId>com.sun.mail</groupId>
//            <artifactId>javax.mail</artifactId>
//            <version>1.5.4</version>
//        </dependency>

    2、配置qq邮箱。开启POP3/SMTP服务

     邮箱--设置--POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务--开启服务：
     POP3/SMTP服务 (如何使用 Foxmail 等软件收发邮件？)已开启 |  关闭

    3、
    smtpHost="smtp.qq.com"
    smtpPassword="ipxczauxtutggecb" 开通smtp服务的授权码
            <smtp name="Mail" subject="Error Log" to="709737588@qq.com,517312606@qq.com" from="1513918351@qq.com"
              replyTo="1513918351@qq.com" smtpHost="smtp.qq.com"  smtpDebug="false" smtpProtocol="smtps"
              smtpUsername="1513918351@qq.com" smtpPassword="ipxczauxtutggecb" smtpPort="465" bufferSize="1024">
              <!--定义error级别日志才发-->
            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
    */


    //endregion

    //traceId 添加实现 参见过滤器 TraceIdFilter
    @GetMapping(value = "/log4j2test")
    public String log4j2Test() throws InterruptedException {
        logger.debug("debug log4j2test ");
        logger.info("info log4j2test ");
        logger.error("error log4j2test ");
//        Thread.sleep(10000);
        int m = Integer.parseInt("m");
        return "log4j2Test";
    }


    @GetMapping(value = "/log4j2test2")
    public String log4j2Test2() {
        logger.debug("debug log4j2test ");
        logger.info("info log4j2test ");
        logger.error("error log4j2test ");
        int m = Integer.parseInt("m");
        return "log4j2Test";
    }
    //endregion


    //region feign
    @GetMapping(value = "/feignTest")
    public String feignTest(String name) {
        return "feignTest - " + name;
    }
    //endregion


    //region MD5
    @GetMapping(value = "/md5")
    public String md5(String password) {
        //md5(Password+UserName)，即将用户名和密码字符串相加再MD5，这样的MD5摘要基本上不可反查。字母+数字，10位以上
        //MD5加密不可逆  比较密码  和MD5加密后的字符串比较
        //对密码进行 md5 加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        return md5Password;
    }
    //endregion

    //region MD5
    @GetMapping(value = "/batchInsert")
    public void batchInsert() {
        //md5(Password+UserName)，即将用户名和密码字符串相加再MD5，这样的MD5摘要基本上不可反查。字母+数字，10位以上
        //MD5加密不可逆  比较密码  和MD5加密后的字符串比较
        //对密码进行 md5 加密

        this.demoProductService.batchInsert();
    }
    //endregion


    //region apacheCommons
    @GetMapping(value = "/apacheCommons")
    public void apacheCommons() {
        //判空不trim: cs == null || cs.length() == 0
        org.apache.commons.lang3.StringUtils.isEmpty(" ");
        //有判空和trim 效果 Character.isWhitespace
        org.apache.commons.lang3.StringUtils.isBlank(" ");
        List<String> list=new ArrayList<>();
        org.apache.commons.collections4.CollectionUtils.isEmpty(list);
    }
    //endregion


}
