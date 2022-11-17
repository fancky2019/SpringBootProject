package com.example.demo.controller;

import cn.hutool.crypto.symmetric.AES;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.example.demo.model.dto.JacksonDto;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.Person;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.pojo.*;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.request.TestRequest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.ValidatorVo;
import com.example.demo.model.vo.DownloadData;
import com.example.demo.model.vo.UploadData;
import com.example.demo.service.demo.DemoProductService;
import com.example.demo.service.demo.PersonService;
import com.example.demo.shiro.ShiroRedisProperties;
import com.example.demo.utility.RSAUtil;
import com.example.demo.utility.RepeatPermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/*
在service类上加注解@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
ConfigurableBeanFactory.SCOPE_PROTOTYPE，即“prototype”
ConfigurableBeanFactory.SCOPE_SINGLETON，即“singleton”
WebApplicationContext.SCOPE_REQUEST，即“request”
WebApplicationContext.SCOPE_SESSION，即“session”

默认Controller、Dao、Service都是单例的。
@Scope bean作用域 @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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


    @Resource
    ApplicationContext applicationContext;

    //Lombox 的注解 @Slf4j 相当于下面语句
    private static final Logger LOGGER = LogManager.getLogger(UtilityController.class);

    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;


    @Autowired
    private ShiroRedisProperties shiroProperties;


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DemoProductService demoProductService;

    @Autowired
    private PersonService personService;

//    @Autowired
//    private FanckyTest fanckyTest;

    @Autowired
    private BeanLife beanLife;


    // @Resource 指定bean 名称,@Autowired 通过 @Qualifier指定具体别名
    @Resource(name = "studentF")
    private Student studentF;

    //指定bean 名称
    @Qualifier("studentD")
    @Autowired
    private Student studentD;


    @Autowired
    private HttpServletRequest httpServletRequest;


    //bean  生命周期 参见 model--pojo--BeanLife SpringLifeCycleBean
    //初始化操作：1、实现 InitializingBean 接口
//    public class UserController implements InitializingBean {
//        // 初始化方法
//        @Override
//        public void afterPropertiesSet() throws Exception {
//           //类初始化操作
//        }
//    }

    //二   @PostConstruct
    // 类初始化调用顺序：
    //（1）构造方法Constructor
    //（2）@Autowired
    //（3）@PostConstruct
    @PostConstruct
    public void init() {

    }


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
     * <p>
     * <p>
     * spring 默认根据枚举名称来映射字段。
     */
    @RequestMapping("/enumParamTest")
    public EnumParamPojo enumParamTest(EnumParamPojo pojo) {
        //名称要对应
        UnitEnum unitEnum = UnitEnum.fromString("ZHi");
        UnitEnum unitEnum1 = UnitEnum.fromString("Tou");

        //如果后端用数据接收。此种使用
        if (1 == UnitEnum.TOU.getValue()) {

        }
        String zhiStr = UnitEnum.ZHI.toString();//Zhi
        String tou = UnitEnum.TOU.toString();//TOU
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
        LOGGER.debug("debug log4j2test ");
        LOGGER.info("info log4j2test ");
        LOGGER.error("error log4j2test ");
//        Thread.sleep(10000);
        int m = Integer.parseInt("m");
        return "log4j2Test";
    }


    @GetMapping(value = "/log4j2test2")
    public String log4j2Test2() {
        LOGGER.debug("debug log4j2test ");
        LOGGER.info("info log4j2test ");
        LOGGER.error("error log4j2test ");
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

    //region MD5 消息摘要算法

    /*
    BASE64 严格地说，属于编码格式，而非加密算法
 主要就是BASE64Encoder、BASE64Decoder两个类，我们只需要知道使用对应的方法即可。
 另，BASE加密后产生的字节位数是8的倍数，如果不够位数以=符号填充。



 Base64就是一种基于64个可打印字符来表示二进制数据的方法
 Base64编码是从二进制到字符的过程
     */
    @GetMapping(value = "/base64")
    public String base64(String src) {
        //        Base64.Encoder  encoder=new Base64.Encoder();
//        encoder.encodeToString()
        //
        String encodeStr = Base64Utils.encodeToString(src.getBytes());
        byte[] de = Base64Utils.decodeFromString(encodeStr);

//        Base64Utils.decodeFromUrlSafeString()
//        Base64Utils.encodeToUrlSafeString()
        return encodeStr;
    }

    //endregion

    //region aes
    @GetMapping(value = "/aes")
    public String aes(String src) {
        //        Base64.Encoder  encoder=new Base64.Encoder();
//        encoder.encodeToString()
        //

        return "";
    }
    //endregion


    //region MD5 消息摘要算法


    /**
     * password：对明文长度没有要求
     *
     * @param password
     * @return
     */
    @GetMapping(value = "/md5")
    public String md5(String password) {
        /*
        MD5散列函数长度是128比特（16字节或32个16进制数字符）
        MD5 加密后的位数有两种：16 位与 32 位。默认使用32位。
         （16 位实际上是从 32 位字符串中取中间的第 9 位到第 24 位的部分）str.substring(8, 24);为提高安全性。


         最好把密码加盐（随机字符串） password+salt,密码和盐 可以按一定规则混合，最简单直接拼接
         */


        //默认32小写
        //md5(Password+UserName)，即将用户名和密码字符串相加再MD5，这样的MD5摘要基本上不可反查。字母+数字，10位以上
        //MD5加密不可逆  比较密码  和MD5加密后的字符串比较
        //对密码进行 md5 加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        String upperStr = md5Password.toUpperCase();
        String str16 = md5Password.substring(8, 24);
        return md5Password;
    }
    //endregion

    //region mybatis batch operation

    //region batchInsert

    /*
    batchInsertSession 的性能比 batchInsert好
    batchInsertSession： 在session 内一条一条插入最后提交
    batchInsert：一次插入提交多条
     */

    @GetMapping(value = "/batchInsert")
    public void batchInsert() {
        this.demoProductService.batchInsert();
    }

    /**
     * 连接字符串添加 &rewriteBatchedStatements=true
     */
    @GetMapping(value = "/batchInsertSession")
    public void batchInsertSession() {
        this.demoProductService.batchInsertSession();
    }

    //endregion

    //region batchUpdate
    /*
   连接字符串添加 &allowMultiQueries=true
     */
    @GetMapping(value = "/batchUpdate")
    public void batchUpdate() {
        this.demoProductService.batchUpdate();
    }
    //endregion

    //region batchDelete
    @GetMapping(value = "/batchDelete")
    public void batchDelete() {
        this.demoProductService.batchDelete();
    }
    //endregion

    //endregion


    //region apacheCommons
    @GetMapping(value = "/apacheCommons")
    public void apacheCommons() {
        //判空不trim: cs == null || cs.length() == 0
        org.apache.commons.lang3.StringUtils.isEmpty(" ");
        //有判空和trim 效果 Character.isWhitespace
        org.apache.commons.lang3.StringUtils.isBlank(" ");
        List<String> list = new ArrayList<>();
        org.apache.commons.collections4.CollectionUtils.isEmpty(list);
    }
    //endregion


    //region 事务传播

    /*
     @Transactional：
    Propagation propagation() default Propagation.REQUIRED;

    Isolation isolation() default Isolation.DEFAULT;
     ESTED：嵌套事务回滚到回滚点。
    NESTED是为被嵌套的方法开启了一个子事务，这个事务与父类使用的是同一个连接。
    REQUIRES_NEW是使用一个全新的事务，这个事务属于另外一条全新的连接。
    两者最重要的体现，就是在多数据源中，REQUIRES_NEW会再次触发一下数据源的获取，而NESTED则不会




    REQUIRED： 没有事务就开启，有事务就加入，不指定的话默认为该类型
    SUPPORTS： 有事务就加入，没有就无事务运行
    MANDATORY： 加入当前事务，如果不存在则抛出异常
    REQUIRES_NEW： 没有就开启，有了挂起原来的，开启新的事务：调用者在老事务，新事物不影响外层食物，外层事务回滚整个事务。
    NOT_SUPPORTED： 有了挂起，没有就无事务运行
    NEVER： 以非事务方式执行，如果存在事务则抛出异常
    Propagation.NESTED: 调用者事务存在调用者事务和被调用者事务分别在两个事务中执行，嵌套事务回滚到回滚点。外层事务回滚整个事务。
     */
    @GetMapping(value = "/propagation")
    public void propagation() {
        try {
            Person person = new Person();
            person.setName("fancky");
            person.setAge(27);
            person.setBirthday(LocalDateTime.now());
            personService.insert(person);
        } catch (Exception e) {
            LOGGER.error("", e);
        }

    }
    //endregion

    //region BeanLife

    @GetMapping(value = "/beanLifeTest")
    public void beanLifeTest() {
        try {

        } catch (Exception e) {
            LOGGER.error("", e);
        }

    }
    //endregion

    //region Jackson

    @GetMapping(value = "/jacksonTest")
    public JacksonDto jacksonTest() {

        JacksonDto jacksonDto = new JacksonDto();
        jacksonDto.setId(1);
        jacksonDto.setCityName("shanghai");
        jacksonDto.setGrade("高二");
        jacksonDto.setCreateTime(LocalDateTime.now());
        jacksonDto.setBirthday(new Date());

        List<Person> people = new ArrayList<>();
        people.add(new Person(1L, "fancky1", 27, LocalDateTime.now()));
        people.add(new Person(2L, "fancky2", 27, LocalDateTime.now()));
        jacksonDto.setPerson(people);

        try {
            String json = mapper.writeValueAsString(jacksonDto);
            JacksonDto dto = mapper.readValue(json, JacksonDto.class);
            int m = 0;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return jacksonDto;
    }
    //endregion


    //region Jackson

    @GetMapping(value = "/starterTest")
    public String starterTest() {
//        return this.fanckyTest.getHost()+":"+this.fanckyTest.getPort();
        return null;
    }
    //endregion


    //region threadExceptionTest

    @GetMapping(value = "/threadExceptionTest")
    public String threadExceptionTest() {
//        return this.fanckyTest.getHost()+":"+this.fanckyTest.getPort();
        threadPoolExceptionTest();
//        threadExceptionTestFun();
        return null;
    }

    private void threadPoolExceptionTest() {

        try {
            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() ->
            {
                //必须在线程内部进行异常处理，无法抛出到外边的另外一个线程。和C#一样
//                Integer m = Integer.parseInt("m");
                try {
                    Integer m = Integer.parseInt("m");
                    int ii = 0;
                } catch (Exception e) {
                    System.out.println("Thead 内部:" + e.getMessage());
                    throw e;
                }
                return 1;
            });

            // get 内部还是通过 LockSupport.unpark()、 LockSupport.park() block 当前线程。知道线程完成
            completableFuture.get();
            int n = 0;
        } catch (Exception e) {
            //外层捕获，没有进入catch
            System.out.println("Thead 外部:" + e.getMessage());
            int m = 0;
        }


        System.out.println("threadPoolExceptionTest Completed");
    }


    /*
    异常不会抛出到主线程，主线程代码已经执行完
     */
    private void threadExceptionTestFun() {

        try {
            Thread thread = new Thread(() ->
            {
                //必须在线程内部进行异常处理，无法抛出到外边的另外一个线程。和C#一样
                Integer m = Integer.parseInt("m");
//                try {
//                    Integer m = Integer.parseInt("m");
//                } catch (Exception e) {
//                    System.out.println("Thead 内部:"+e.getMessage());
//                    throw e;
//                }

            });

            //外部主线程已经执行完，如果内部线程抛异常将不会进入外部线程
            thread.start();
        } catch (Exception e) {
            //外层捕获，没有进入catch
            System.out.println("Thead 外部:" + e.getMessage());
            int m = 0;
        }


        System.out.println("threadExceptionTestFun Completed");
    }


    //endregion


    //region form-data 文件和数据 @RequestPart
    //不指定盘符要加入 commons-io 依赖


    //文件：单个文件用 MultipartFile file 接收，多个用MultipartFile[] files.注意别名设置
    //     前台postman 设置 点击 description 右侧... 选中 content-type .在contentType设置multipart/form-data
    //表单数据：前台postman 设置json 字符串提交
    //     前台postman 设置 点击 description 右侧... 选中 content-type .在contentType设置   application/json

    @PostMapping(value = "/saveFormDta")
    public ReturnResult saveFormDta(@RequestPart(value = "files", required = false) MultipartFile[] files, @RequestPart("testRequest") TestRequest testRequest) {
        ReturnResult<Void> response = new ReturnResult();

        try {

            List<String> fileNames = saveFiles(files);
            //获取body中的参数
//            String value = (String)request.getAttribute("paramName");
            String name = testRequest.getName();
        } catch (IOException e) {
            response.setSuccess(false);
            response.setCode(500);
            LOGGER.error("", e);
        }

        return response;
    }

    //    @Async
    public List<String> saveFiles(MultipartFile[] files) throws IOException {
        List<String> tempFiles = new ArrayList<String>();
        if (files != null && files.length > 0) {
            //遍历文件
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    String dateStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rootPath = System.getProperty("user.dir");
                    String directory = rootPath + "\\uploadfiles" + "\\" + dateStr + "\\";
//                    String directory = "\\uploadfiles" + "\\" + dateStr + "\\";
                    File destFile = new File(directory);
                    //判断路径是否存在,和C#不一样。她判断路径和文件是否存在
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }

                    //获取文件名称
                    String sourceFileName = file.getOriginalFilename();
                    //写入目的文件
                    String fileFullName = directory + sourceFileName;
                    File existFile = new File(fileFullName);
                    if (existFile.exists()) {
                        existFile.delete();
                    }
                    //用 file.transferTo 要指定盘符，不然报错找不到文件路径
                    //引入commons-io 依赖，用下面方法不用指定盘符
                    file.transferTo(existFile);
//                    FileUtils.copyInputStreamToFile(file.getInputStream(), existFile);
                    tempFiles.add(fileFullName);
                }
            }
        }
        return tempFiles;
    }

    //endregion

    //region EasyExcel
    //注意：easyExcel 和easyPoi 不兼容，两个不能同时引用，否则easyExcel 下载的excel 打不开
    //     easyexcel 的性能不easypoi性能好点。


    //postman  send and download

    /**
     * https://easyexcel.opensource.alibaba.com/docs/current/quickstart/write
     * 文件下载（失败了会返回一个有部分数据的Excel）
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DownloadData}
     * <p>
     * 2. 设置返回的 参数
     * <p>
     * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     */
    @GetMapping("excelDownload")
    public void download(HttpServletResponse response, DownloadData query) throws IOException {

        try {
            // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), DownloadData.class).sheet("表名称").doWrite(data());

            int mm = 0;
        } catch (Exception ex) {
            String msg = ex.getMessage();
            int m = 0;
        }
    }

    private List<DownloadData> data() {
        List<DownloadData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DownloadData data = new DownloadData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    /**
     * 文件上传
     * 1. 创建excel对应的实体对象
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器
     * 3. 直接读即可
     */
    @PostMapping("excelUpload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        List<UploadData> list = new ArrayList<UploadData>();
        EasyExcel.read(file.getInputStream(), UploadData.class, new ReadListener<UploadData>() {

                    /**
                     * 这个每一条数据解析都会来调用
                     * @param o
                     * @param analysisContext
                     */
                    @Override
                    public void invoke(UploadData o, AnalysisContext analysisContext) {

                        list.add(o);

                    }

                    /**
                     *所有的都读取完 回调 ，
                     * @param analysisContext
                     */
                    @Override
                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                    }
                }
        ).sheet().doRead();

        int size = list.size();
        return "success";
    }
    //endregion

    @GetMapping("beanTest")
    public void BeanTest() {
        String na1 = studentD.getName();
        String na2 = studentF.getName();
        int m = 0;
    }

    @GetMapping("rsaTest")
    public MessageResult<Void> rSAUtilTest() throws Exception {
        try {


            String data = "ddddddddddddddddddddddddddddd54454554dddddddddddddd" +
                    "ddddddddddddddddddddddddddddd544554ddddddddddddddd" +
                    "ddddddddddddddddddddddddddd5454ddddddddddddddd" +
                    "ddddddddddddddddddddd454554dddddddddddddddddddddd" +
                    "dsrgregergredssssssssssssss333333333333333535355" +
                    "dssssssssss44444444444444444444";
            byte[] encryptData = RSAUtil.encrypt(data.getBytes());
            String decryptData = new String(RSAUtil.decrypt(encryptData));

            String signData = RSAUtil.sign(data.getBytes(), RSAUtil.getPrivateKey());
            boolean re = RSAUtil.verify(data.getBytes(), signData, RSAUtil.getPublicKey());
            int m = 0;
        } catch (Exception e) {

            int m = 0;
            throw e;
        }

        return new MessageResult<>();
    }

    @RepeatPermission
    @GetMapping("repeat")
    public MessageResult<Void> repeat() {

        MessageResult<Void> messageResult = new MessageResult<>();
        messageResult.setSuccess(true);
        return messageResult;
    }

    private void localJar() {
        /*
       打第三方jar, maven 插件配置中添加 <skip>true</skip>-->
        <!-- 打第三方jar 去掉打包的jar目录有BOOT-INF文件夹，项目引入jar后，找不到程序包，找不到指定bean-->
<!--                    <skip>true</skip>-->
         */
        com.fancky.model.entity.ModelTest model = new com.fancky.model.entity.ModelTest();


    }

    @RequestMapping(value = "/getCookies", method = RequestMethod.GET)
    public String getCookies(HttpServletResponse response) {
        //HttpServletRequest  装请求信息的类
        //HttpServletResponse  装相应信息的类
        Cookie cookie = new Cookie("sessionId", "10001");
        response.addCookie(cookie);
        return "恭喜获得cookies信息成功";
    }

    //SpringMVC可以通过入参的方式绑定HttpServletRequest和HttpServletResponse
    // (SpringMVC在调用处理器时会自动创建对应的HttpServletRequest和HttpServletReponse对象并传入适配的控制器方法中)
    @RequestMapping(path = "/httpServletRequestResponse")
    public String httpServletRequestResponse(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("sessionId")) {
                String sessionId = cookie.getValue();
            }
        }
        System.out.println(request.toString() + response.toString());


        //通用获取HttpServletRequest、HttpServletResponse
        //获取response对象
        HttpServletResponse response1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //获取request对象
        HttpServletRequest request1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();


        return "helloWorld";
    }


    @GetMapping("/fileTest")
    public List<String> fileTest() {
        //D:\\work\\project\\git\\java\\SpringBootProject
        String rootPath = System.getProperty("user.dir");

        String filePath = "/configfile/name.txt";


        String content = "";
        List<String> strList = new ArrayList<>();
        try {
            //idea 中可以正常运行，达成jar报错：找不到文件
//            ClassPathResource resource = new ClassPathResource(filePath);
//            File file = resource.getFile();
//            content = FileUtils.readFileToString(file, "UTF-8");


//            content = IOUtils.resourceToString(filePath, Charset.forName("UTF-8"));



            //注入applicationContext
//    Map<String, Object> enums =this.applicationContext.getBeansWithAnnotation(QLExpressionEnumDescription.class);

            InputStream inputStream = this.getClass().getResourceAsStream(filePath);
            strList = IOUtils.readLines(inputStream, Charset.forName("UTF-8"));


            int m = 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        try {
//            //append 没有换行符
//            StringBuilder fileContent = new StringBuilder();
//            InputStream is = this.getClass().getResourceAsStream(filePath);
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//            String data = null;
//            while ((data = br.readLine()) != null) {
//                fileContent.append(data + System.getProperty("line.separator"));
//            }
//            br.close();
//            isr.close();
//            is.close();

            int m = 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return strList;
    }

    @GetMapping("/enableConfigurationProperties")
    public String enableConfigurationProperties() {
        return shiroProperties.getHost();
    }


    @GetMapping("/pageHelper")
    public PageData<DemoProduct> pageHelper(DemoProductRequest request) {
        return demoProductService.pageHelper(request);

    }
    @GetMapping("/getPageData")
    public PageData<DemoProduct> getPageData(DemoProductRequest request) {
        return demoProductService.pageHelper(request);

    }
}
