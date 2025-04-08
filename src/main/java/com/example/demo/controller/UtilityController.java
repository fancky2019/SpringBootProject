package com.example.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.demo.config.ConfigModelProperty;
import com.example.demo.easyexcel.DropDownSetField;
import com.example.demo.easyexcel.ExcelStyleConfig;
import com.example.demo.easyexcel.GXDetailListVO;
import com.example.demo.easyexcel.ResoveDropAnnotationUtil;
import com.example.demo.easyexcel.handler.DropDownCellWriteHandler;
import com.example.demo.eventbus.MyCustomEvent;
import com.example.demo.listener.UserRegisterService;
import com.example.demo.model.dto.JacksonDto;
import com.example.demo.model.entity.demo.DemoProduct;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.entity.demo.Person;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.impot.ImportModelTest;
import com.example.demo.model.pojo.*;
import com.example.demo.model.request.DemoProductRequest;
import com.example.demo.model.request.MqMessageRequest;
import com.example.demo.model.request.TestRequest;
import com.example.demo.model.response.MqMessageResponse;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.model.viewModel.ValidatorVo;
import com.example.demo.model.vo.DownloadData;
import com.example.demo.model.vo.UploadData;
import com.example.demo.model.wmsservicemodel.ShipOrderInventoryDetailDto;
import com.example.demo.rabbitMQ.mqtt.MqttProduce;
import com.example.demo.rocketmq.RocketmqTest;
import com.example.demo.service.RetryService;
import com.example.demo.service.TokenService;
import com.example.demo.service.api.FeignClientTest;
import com.example.demo.service.api.WmsService;
import com.example.demo.service.demo.*;
import com.example.demo.service.elasticsearch.ShipOrderInfoService;
import com.example.demo.shiro.ShiroRedisProperties;
import com.example.demo.sse.IResponseBodyEmitterService;
import com.example.demo.sse.ISseEmitterService;
import com.example.demo.utility.RSAUtil;
import com.example.demo.utility.RepeatPermission;
//import com.example.demo.utility.ApplicationContextAwareImpl;
import com.example.demo.utility.TraceIdCreater;
import com.example.demo.utility.TraceIdHolder;
import com.example.fanckyspringbootstarter.service.ToolService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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




//region mysql


failOverReadOnly=false
控制故障转移行为：
当 failOverReadOnly=false 时，如果主数据库（可读写）发生故障，连接 不会 切换到只读模式的备用数据库。这意味着应用程序不会尝试连接到只读副本，而是会直接报错或等待主数据库恢复。
当 failOverReadOnly=true 时，如果主数据库故障，连接可以切换到只读模式的备用数据库，但应用程序需要确保只执行读操作（不能执行写操作


当设置为 false 时，应用程序不会切换到只读的从数据库，而是直接报错或等待主数据库恢复。
当设置为 true 时，应用程序可以切换到只读的从数据库，但只能执行读操作。

 */

//MySQL 自动故障转移（Automatic Failover）是指在主数据库（Master）发生故障时，系统能够自动检测到故障并将从数据库（Slave）
// 提升为新的主数据库， 同时更新应用程序的连接配置，以确保系统的高可用性。


/*
mysql  如何自动检测到故障并将从数据库（Slave）提升为新的主数据库，同时更新应用程序的连接配置

方法 2：Orchestrator（官方推荐）
Orchestrator 是一个开源的 MySQL 高可用工具，支持 主从复制（Replication） 和 Group Replication，可以自动提升新的 Master。

工作流程
监控主库（Master）。
发现主库故障后，选择合适的 Slave 作为新 Master，并自动切换主从关系。
支持 自动 DNS 更新 或 VIP 变更，让应用程序透明地连接到新 Master。
优点

轻量级，MySQL 官方推荐。
Web 界面可视化管理数据库拓扑结构。
适用于大规模 MySQL 集群。
缺点

需要额外部署 Orchestrator 服务器。
 */
//endregion


@Api  //方法上  @ApiOperation()
@Slf4j
@RestController
@RequestMapping("/utility")
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UtilityController {


    @Resource
    ApplicationContext applicationContext;

    //Lombox 的注解 @Slf4j 相当于下面语句
    private static final Logger LOGGER = LogManager.getLogger(UtilityController.class);

    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;


    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ShiroRedisProperties shiroProperties;

    @Autowired
    private RocketmqTest rocketmqTest;


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DemoProductService demoProductService;

    @Autowired
    private IPersonService personService;
    @Autowired
    private TokenService tokenService;
//    @Autowired
//    private FanckyTest fanckyTest;

    @Autowired
    private BeanLife beanLife;

    @Autowired
    private IProductTestService productTestService;

    @Autowired
    private IMqMessageService mqMessageService;

    @Autowired
    private IProductTestService productTestServiceB;

    @Autowired
    private CacheService cacheService;

    @Autowired
    Cache<String, Object> caffeineCache;

    @Autowired
    private ConfigModelProperty configModelProperty;

    // @Resource 指定bean 名称,@Autowired 通过 @Qualifier指定具体别名
    @Resource(name = "studentF")
    private Student studentF;

    //指定bean 名称
    @Qualifier("studentD")
    @Autowired
    private Student studentD;


    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ShipOrderInfoService shipOrderInfoService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private WmsService wmsService;

    @Autowired
    private FeignClientTest feignClientTest;


    @Autowired
    private Executor threadPoolExecutor;
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
    public MessageResult<Integer> postTest(@RequestParam Integer id, @RequestParam String eosorder, @RequestParam BigDecimal eosbalance, @RequestParam Boolean relativestate) {

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
        paramNames.forEach(p -> {
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
    public Student getPostBody(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //请求头
        String value = request.getHeader("Content-Type");
        response.setHeader("Content-Type", value);
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
        //6dff2291fe2e822de2e8068a182c4759 .32字符
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
        //mybatis
//        this.demoProductService.batchUpdate();
//mybatis plus
        productTestService.updateBatchByIdTest();
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

    /**
     * 事务隔离：全局搜索事务隔离  OrderManagerService
     *
     * @Transactional： Propagation propagation() default Propagation.REQUIRED;
     *
     * Isolation isolation() default Isolation.DEFAULT;
     * NESTED：嵌套事务回滚到回滚点。
     * NESTED是为被嵌套的方法开启了一个子事务，这个事务与父类使用的是同一个连接。
     * REQUIRES_NEW是使用一个全新的事务，这个事务属于另外一条全新的连接。
     * 两者最重要的体现，就是在多数据源中，REQUIRES_NEW会再次触发一下数据源的获取，而NESTED则不会
     *
     *
     * 事务传播：同一个事务要回滚都回滚，
     *         不同事物，未捕捉异常都回滚
     *                捕捉异常： REQUIRES_NEW  互不影响：外部异常内部不回滚，内部异常外部不回滚
     *                         NESTED        外部异常回滚整个事务，内部异常还原到还原点
     *
     * REQUIRED： 没有事务就开启，有事务就加入，不指定的话默认为该类型
     * SUPPORTS： 有事务就加入，没有就无事务运行
     * MANDATORY： 加入当前事务，如果不存在则抛出异常
     * REQUIRES_NEW： 没有就开启，有了挂起原来的，开启新的事务：调用者在老事务，新事物不影响外部事务3%%23%%-1。
     * NOT_SUPPORTED： 有了挂起，没有就无事务运行
     * NEVER： 以非事务方式执行，如果存在事务则抛出异常3
     * Propagation.NESTED: 调用者事务存在调用者事务和被调用者事务分别在两个事务中执行，嵌套事务回滚到回滚点。外层事务回滚整个事务。
     */
    @GetMapping(value = "/propagation")
    public void propagation() {
//        Person person = new Person();
//        person.setName("fancky");
//        person.setAge(27);
//        person.setBirthday(LocalDateTime.now());
//        personService.insert(person);


        personService.proTest();
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
            CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
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


    /**
     * 异常不会抛出到主线程，主线程代码已经执行完
     */
    private void threadExceptionTestFun() {

        try {
            Thread thread = new Thread(() -> {
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

    // content-type multipart/form-data
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

    //region EasyExcel 导入导出excel
//    EasyExcel 不能设置数字格式、日期格式。貌似excel 中设置了数字格式 也能输入中文
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
     *
     *
     * excel 单个sheet 最大100W，如果数据量过大可分页查询，批量写入sheet,每次写5000条查询的数据
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

    @ApiOperation(value = "exportExcel")
    @GetMapping(value = "/exportExcel")
    public void exportExcel(HttpServletResponse response, @ApiParam("共享协议号") @RequestParam String gbAgrtCode) throws IOException {

        GXDetailListVO GXDetailListVO = new GXDetailListVO();
        List<GXDetailListVO> data = new LinkedList<>();
        data.add(GXDetailListVO);
        exportExcel("exportExcelTest", response, data);

    }

    /**
     *easyexcel 分页导出
     * exportByPage 批量导出
     *importExcel 批量导出
     */
    @ApiOperation(value = "exportByPage")
    @PostMapping(value = "/exportByPage")
    public void exportByPage(@RequestBody DemoProductRequest request) throws IOException, NoSuchFieldException, IllegalAccessException {

        this.productTestService.exportByPage(httpServletResponse, request);
    }

    /**
     * 导出模板就是导出的时候设置data 为null
     * @throws IOException
     */
    @ApiOperation(value = "exportDemoProductTemplate")
    @GetMapping(value = "/exportDemoProductTemplate")
    public void exportDemoProductTemplate() throws IOException {
        this.productTestService.exportDemoProductTemplate(httpServletResponse);
    }

    @ApiOperation(value = "importExcelProductTest")
    @PostMapping(value = "/importExcelProductTest")
    public void importExcelProductTest(MultipartFile file) throws IOException {
        this.productTestService.importExcelProductTest(httpServletResponse, file);
//        List<ProductTest> list = new ArrayList<ProductTest>();
//        EasyExcel.read(file.getInputStream(), ProductTest.class, new ReadListener<ProductTest>() {
//
//                    /**
//                     * 这个每一条数据解析都会来调用
//                     * @param o
//                     * @param analysisContext
//                     */
//                    @Override
//                    public void invoke(ProductTest o, AnalysisContext analysisContext) {
////                       注意://实体对象设置 lombok 设置    @Accessors(chain = false) 禁用链式调用，否则easyexcel读取时候无法生成实体对象的值
//
//                        int m = 0;
//                        //跳过空白行
//                        if (o.getId() != null) {
//                            list.add(o);
//                        }
//
//
//                    }
//
//                    /**
//                     *所有的都读取完 回调 ，
//                     * @param analysisContext
//                     */
//                    @Override
//                    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
//
//                    }
//
//                    @Override
//                    public void onException(Exception exception, AnalysisContext context) throws Exception {
//                        int m = 0;
////                        CellDataTypeEnum
//                        throw exception;
//                    }
//                }
//        ).sheet().doRead();
//
//        int size = list.size();

    }


    /**
     * easyexcel 批量导入
     * exportByPage 批量导出
     *importExcel 批量导出
     * @param file
     * @throws IOException
     */
    @ApiOperation(value = "importExcel")
    @PostMapping(value = "/importExcel")
    public void importExcel(MultipartFile file) throws IOException {

        List<GXDetailListVO> list = new ArrayList<GXDetailListVO>();
        //保存到数据库的阈值
        final int SAVE_DB_SIZE = 5000;
        EasyExcel.read(file.getInputStream(), GXDetailListVO.class, new ReadListener<GXDetailListVO>() {

            /**
             * 这个每一条数据解析都会来调用
             * @param o
             * @param analysisContext
             */
            @Override
            public void invoke(GXDetailListVO o, AnalysisContext analysisContext) {
                int m = 0;
                list.add(o);
                if (SAVE_DB_SIZE == list.size()) {
                    //保存到数据库，可开启一个线程执行保存。里面进行校验，记录未能成功插入的数据
                    //save()
//                            list.clear();
                }

            }

            /**
             *所有的都读取完 回调 ，
             * @param analysisContext
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }

            @Override
            public void onException(Exception exception, AnalysisContext context) throws Exception {
                int m = 0;
//                        CellDataTypeEnum
                throw exception;
            }
        }).sheet().doRead();

        int size = list.size();

    }

    /**
     * 导出excel
     *
     * @param fileName
     * @param response
     * @param data     导出模板，1 导出错误信息，2 导出数据
     * @throws IOException
     */
    private void exportExcel(String fileName, HttpServletResponse response, List<GXDetailListVO> data) throws IOException {
        prepareResponds(fileName, response);
        ServletOutputStream outputStream = response.getOutputStream();
        // 获取改类声明的所有字段
        Field[] fields = GXDetailListVO.class.getDeclaredFields();
        // 响应字段对应的下拉集合
        Map<Integer, String[]> map = new HashMap<>();
        Field field = null;
        // 循环判断哪些字段有下拉数据集，并获取
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            // 解析注解信息
            DropDownSetField dropDownSetField = field.getAnnotation(DropDownSetField.class);
            if (null != dropDownSetField) {
                String[] sources = ResoveDropAnnotationUtil.resove(dropDownSetField);
                if (null != sources && sources.length > 0) {
                    map.put(i, sources);
                }
            }
        }
        //多个sheet页写入
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.autoCloseStream(true);
//        if (flag == 0 || flag == 2) {
        builder.registerWriteHandler(new ExcelStyleConfig(Lists.newArrayList(20), null, null));
        builder.head(GXDetailListVO.class);
//        } else {
//            builder.registerWriteHandler(new ExcelStyleConfig(null,null,null));
//            builder.head(GXDetailListLogVO.class);
//        }
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "共享明细清单").build();
        builder.registerWriteHandler(new DropDownCellWriteHandler(map));
        builder.file(outputStream);

        //不能重命名，重命名就没有XLSX格式后缀
        builder.excelType(ExcelTypeEnum.XLSX);
        ExcelWriter writer = builder.build();
        writer.write(data, sheet1);
        writer.finish();

        //ExcelWriter实现Closeable 接口，内部close 调用finish, finish 内会执行关闭操作
//        outputStream.flush();
//        outputStream.close();
    }

    /**
     * 将文件输出到浏览器(导出)
     */
    private static void prepareResponds(String fileName, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + fileName + ExcelTypeEnum.XLSX.getValue());
    }

    /**
     * 文件上传 导入excel。excel 中的列可以不和实体类字段严格匹配
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
        }).sheet().doRead();

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


            String data = "ddddddddddddddddddddddddddddd54454554dddddddddddddd" + "ddddddddddddddddddddddddddddd544554ddddddddddddddd" + "ddddddddddddddddddddddddddd5454ddddddddddddddd" + "ddddddddddddddddddddd454554dddddddddddddddddddddd" + "dsrgregergredssssssssssssss333333333333333535355" + "dssssssssss44444444444444444444";
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

    /**
     * 如果maxAge属性为正数：则表示该Cookie会在maxAge秒之后自动失效。浏览器会将maxAge为正数的Cookie持久化，
     * 即写到对应的Cookie文件中。无论客户关闭了浏览器还是电脑，只要还在maxAge秒之前，登录网站时该Cookie仍然有效。
     * 下面代码中的Cookie信息将永远有效。
     * <p>
     * <p>
     * 如果maxAge为负数：则表示该Cookie仅在本浏览器窗口以及本窗口打开的子窗口内有效，关闭窗口后该Cookie即失效。
     * maxAge为负数的Cookie，为临时性Cookie，不会被持久化，不会被写到Cookie文件中。Cookie信息保存在浏览器内存中，
     * 因此关闭浏览器该Cookie就消失了。Cookie默认的maxAge值为–1。
     * <p>
     * <p>
     * cookie 删除：新建一个同名的Cookie，添加到response中覆盖原来的Cookie。
     * 修改：只需要新建一个同名的Cookie，并将maxAge设置为0，并添加到response中覆盖原来的Cookie
     */
    @RequestMapping(value = "/getCookies", method = RequestMethod.GET)
    public String getCookies(HttpServletResponse response) {
        //HttpServletRequest  装请求信息的类
        //HttpServletResponse  装相应信息的类
        Cookie cookie = new Cookie("sessionId", "10001");
        //cookie.setMaxAge(0);//默认-1，设置0 客户端删除cookie
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
            //idea 中可以正常运行，打成jar报错：找不到文件
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

    @GetMapping("/mybatisPlusTest")
    public MessageResult<String> mybatisPlusTest(DemoProductRequest request) throws InterruptedException {
        productTestService.mybatisPlusTest();
        return MessageResult.success();

    }

    @GetMapping("/beanConstructor")
    public String beanConstructor() {
        personService.test();
        return "completed";

    }

    @GetMapping("/stopWatch")
    public MessageResult<String> stopWatch() throws Exception {


        StopWatch stopWatch1 = new StopWatch();


        stopWatch1.start("task1");

        //stop 之后就无法获取当前任务名称
        String taskName = stopWatch1.currentTaskName();
        stopWatch1.stop();

//            System.out.println(stopWatch.prettyPrint());
//            System.out.println(stopWatch.currentTaskName()+" "+stopWatch.getLastTaskTimeMillis()  +" toatal " +stopWatch.getTotalTimeMillis() );
        System.out.println(taskName + " " + stopWatch1.getLastTaskTimeMillis() + " toatal " + stopWatch1.getTotalTimeMillis());


        StopWatch stopWatch = new StopWatch("stopWatch1");
        stopWatch.start("work1");
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        //第二次检测不会重置之前的时间，重新new  重置时间
        stopWatch = new StopWatch("stopWatch2");
        stopWatch.start("work2");
        Thread.sleep(3000);
        stopWatch.stop();


        System.out.println(stopWatch.prettyPrint());

        MessageResult<String> messageResult = new MessageResult<>();
        messageResult.setMessage("completed");
        messageResult.setData(null);
        return messageResult;

    }

    //region ehcache
    @GetMapping("/cacheServiceGet")
    public MessageResult<ProductTest> cacheServiceGet(int id) {
        MessageResult<ProductTest> messageResult = new MessageResult<>();
        messageResult.setMessage("completed");
        ProductTest productTest = this.cacheService.getProductTest(id);
        messageResult.setData(productTest);
        return messageResult;
    }

    @PostMapping("/cacheServicePut")
    public String cacheServicePut(@RequestBody ProductTest request) {
        this.cacheService.cacheServicePut(request);
        return "completed";
    }

    @PostMapping("/cacheServiceEvict")
    public String cacheServiceEvict(@RequestBody ProductTest request) {
        this.cacheService.cacheServiceEvict(request.getId());
        return "completed";
    }
    //endregion

    //region sse (sever-sent event)
    @Autowired
    private ISseEmitterService sseEmitterService;

    /**
     * 前段页面 user /index.html
     *http://127.0.0.1:8081/sbp/utility/sseConnect/1
     * @param userId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/sseConnect/{userId}")
    @ApiOperation(value = "建立Sse链接", notes = "建立Sse链接", httpMethod = "GET")
    public SseEmitter push(@PathVariable("userId") String userId) throws Exception {
        return sseEmitterService.createSseConnect(userId);
    }


    @GetMapping(value = "/sseSendMsg")
    public void sendMsg(String userId) {
        sseEmitterService.sendMsgToClient(userId, "test");
    }

    @GetMapping(value = "/close/{userId}")
    public void close(@PathVariable("userId") String userId) {
        sseEmitterService.closeSseConnect(userId);
    }
    //endregion

    @GetMapping(value = "/rocketmqTest/{topic}")
    public void rocketmqTest(@PathVariable("topic") String topic) {
        rocketmqTest.test(topic);
        //automapper
        //浅拷贝   MapStruct
//        BeanUtils.copyProperties();
    }


    @Autowired
    private IResponseBodyEmitterService responseBodyEmitterService;


    /**
     * 前段页面 user /index.html
     *http://127.0.0.1:8081/sbp/utility/createResponseBodyEmitterConnect/1
     * @param userId
     * @return
     * @throws Exception
     */
//    @GetMapping(value = "/createResponseBodyEmitterConnect/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @GetMapping(value = "/createResponseBodyEmitterConnect/{userId}")
    @ApiOperation(value = "建立Sse链接", notes = "建立Sse链接", httpMethod = "GET")
    public ResponseBodyEmitter createResponseBodyEmitterConnect(@PathVariable("userId") String userId) throws Exception {
        return responseBodyEmitterService.createResponseBodyEmitterConnect(userId);
    }

    //RequestMapping  RequestMethod

    /**
     * 更新
     *
     * @param id
     * @param age
     * @return
     */
    @PutMapping("/modify/{id}/{age}")
    public String modify(@PathVariable Integer id, @PathVariable Integer age) {
        return "更新资源，执行put请求方式：id=" + id + " aeg=" + age;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        //DELETE
        String httpMethod = httpServletRequest.getMethod();
        return "删除资源，执行delete请求方式：id=" + id;

    }

    @GetMapping(value = "/caffeineTest")
    public void caffeineTest() {

        /*
        jdk1.8 只能用 V2.9.*   v3.0.*要java11
         可单独使用也可以结合springboot cache 使用。代码参见 CacheService
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
            <version>2.9.3</version>
        </dependency>

         */
        String key = "1";
        Object object = caffeineCache.getIfPresent(key);
        if (object != null) {
            ProductTest productTest = (ProductTest) object;
        } else {
            ProductTest productTest = new ProductTest();
            BigInteger bigDecimal = BigInteger.valueOf(1);
            productTest.setId(bigDecimal);
            // 加入缓存
            caffeineCache.put(String.valueOf(productTest.getId()), productTest);
        }

    }

    @Autowired
    private UserRegisterService userRegisterService;

    @GetMapping(value = "/listenerTest")
    public void listenerTest() {
        userRegisterService.registerUser("fancky");
    }

    @Autowired
    private MqttProduce mqttProduce;

    @GetMapping(value = "/mqttTest")
    public void mqttTest(String msg) {
        /*
        QoS 0（最多一次）：消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这个级别可用于如下情况，环境传感器数据，丢失一次数据无所谓，因为不久后还会有第二次发送。
        QoS 1（至少一次）：确保消息到达，但消息重复可能会发生。
        QoS 2（只有一次）：确保消息到达一次。这个级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果。
         */
        int qos = 1;
        //retained = true 只会保留最后一条消息
        boolean retained = false;
        String topic = "topic1";

        mqttProduce.publish(qos, retained, topic, msg);
    }

    /**
     * @param apiName 后端要请求保存的接口名称
     * @return
     */
    @GetMapping(value = "/getRepeatToken")
    public MessageResult<String> getRepeatToken(String apiName) {
        return tokenService.getRepeatToken(apiName);
    }

    @Autowired
    private RetryService retryService;

    @GetMapping(value = "/retryTest")
    public MessageResult<Void> retryTest(String msg) {
        /*
        启动类添加 @EnableRetry,1.2.1 不要制定版本号，maven 最新的可能jdk 版本不匹配
        proxyTargetClass 默认基于JDK 动态代理


        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>2.0.4</version>
        </dependency>
         */


        return retryService.test(5);
    }

    //region @Import
    @Autowired
    ImportModelTest importModelTest22;

    @GetMapping(value = "/importTest")
    public MessageResult<List<String>> importTest() {

        List<String> nameList = new ArrayList<>();
//        ApplicationContext applicationContext = ApplicationContextAwareImpl.getApplicationContext();
        ApplicationContext applicationContext = this.applicationContext;
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        // 遍历Spring容器中的beanName
        for (String beanDefinitionName : beanDefinitionNames) {
            if (beanDefinitionName.toLowerCase().contains("importModelTest".toLowerCase())) {
                nameList.add(beanDefinitionName);
            }
        }
        String name = importModelTest22.getFistName();
        return MessageResult.success(nameList);
    }
    //endregion

    //region starterTest  项目fancky-spring-boot-starter

    /**
     * 1、定义字段配置文件类
     * 2、定义自动配置类：启用配置属性加入IOC、starter业务类通过bean加入IOC
     * 3、添加spring.factories文件配置自动装配
     * 4、pom配置无main类启动 install到本地仓库
     * 5、其他工程引入依赖
     */
    @Autowired
    ToolService toolService;

    @GetMapping(value = "/starterTest")
    public MessageResult<String> starterTest() {
        return MessageResult.success(toolService.CommonFun());
    }
    //endregion

    @GetMapping(value = "/applicationContextTest")
    public MessageResult<String> applicationContextTest() {
        Object obj = this.applicationContext.getBean("demoProductService");
        return MessageResult.success();

    }

    /**
     * 此类 propagation 事务传播方法
     * 手动事务OrderManagerService 方法 addOrder
     * TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
     */

    @GetMapping(value = "/transactionalTest")
    public void transactionalTest() {
        Person person = new Person();
        person.setName("fancky8888");
        person.setAge(27);
        person.setBirthday(LocalDateTime.now());
        personService.insert(person);
    }

    /**
     * 事务同步  更新覆盖 、事务传播 版本号
     * 事务aop,代码执行完长事务未提交
     * @param i
     * @throws InterruptedException
     */
    @GetMapping(value = "/transactionalSynchronizedTest")
    public void transactionalSynchronizedTest(Integer i) throws InterruptedException {

        personService.transactionalSynchronizedTest(i);
    }

    /**
     * 事务同步  更新覆盖 、事务传播 版本号
     * 事务aop,代码执行完长事务未提交
     * @param i
     * @throws InterruptedException
     */
    @GetMapping(value = "/transactionalSynchronizedTest1")
    public void transactionalSynchronizedTest1(Integer i) throws InterruptedException {
//        ResponseEntity
        personService.transactionalSynchronizedTest1(i);
    }

    /**
     * 解决并发下 redissonLock 释放了 事务未提交
     * @throws InterruptedException
     */
    @GetMapping(value = "/transactionalRedission")
    public void transactionalRedission() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() -> {
                try {
                    final int j = finalI;
                    productTestService.transactionalRedission(j);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

//        productTestService.transactionalRedission(1);

    }

    /**
     * 事务同步 更新覆盖 、事务传播 版本号
     * @param i
     * @throws InterruptedException
     */
    @GetMapping(value = "/manualCommitTransaction")
    public void manualCommitTransaction(Integer i) throws InterruptedException {
        personService.manualCommitTransaction();
    }

    /**
     * 事务同步 更新覆盖 、事务传播 版本号
     * @param executeException
     * @throws InterruptedException
     */
    @GetMapping(value = "/transactionTemplateTest")
    public void transactionTemplateTest(boolean executeException) throws InterruptedException {
        personService.transactionTemplateTest(executeException);
    }

    /**
     * 更新覆盖：两个事务对数据库写，要加悲观锁（redisson，for update 容易死锁）使得事务串化。更新的时候加上版本号条件
     * 如果返回受影响行为0，更新失败。抛异常，否则继续执行。
     *
     * add、delete、update 、lock in share mode 当前读加锁
     * 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。for update
     * @return
     */
    @GetMapping(value = "/coverUpdateTestOne")
    public MessageResult<Void> coverUpdateTestOne(String productName) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            productTestService.coverUpdateTestOne(productName);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;
    }

    /**
     * 更新覆盖：两个事务对数据库写，要加悲观锁（redisson，for update 容易死锁）使得事务串化。更新的时候加上版本号条件
     * 如果返回受影响行为0，更新失败。抛异常，否则继续执行。
     *
     * add、delete、update 、lock in share mode 当前读加锁
     * 在多事务同时修改同一条记录的情况下，MySQL 会自动对涉及的数据行加上写锁（排他锁）。for update
     * @return
     */
    @GetMapping(value = "/coverUpdateTestTwo")
    public MessageResult<Void> coverUpdateTestTwo(String productName) {
        MessageResult<Void> message = new MessageResult<>();
        try {
            productTestService.coverUpdateTestTwo(productName);
        } catch (Exception e) {
            message.setSuccess(false);
            message.setMessage(e.getMessage());
        }
        return message;
    }


    /**
     *
     * 在关闭 Spring Boot 服务时，通常应该使用 kill -15，而不是 kill -2
     *
     *
     * # 开启优雅停机，默认值：immediate 为立即关闭
     * server.shutdown=graceful
     *
     * # 设置缓冲期，最大等待时间，默认：30秒
     * spring.lifecycle.timeout-per-shutdown-phase=60s
     *
     * timeout-per-shutdown-phase:
     *当 Spring Boot 接收到关闭信号（如 SIGTERM 或通过 Actuator 的 /shutdown 端点）时，会进入优雅关闭流程。
     * 在此期间，Spring Boot 会等待未完成的任务（如正在处理的 HTTP 请求、异步任务等）继续执行，直到任务完成或超时。
     * timeout-per-shutdown-phase 用于设置这个等待的最大时间。
     *
     *
     * 优雅停机设置：
     *
     * server:
     *   shutdown: GRACEFUL # GRACEFUL/IMMEDIATE (默认:IMMEDIATE)
     * spring:
     *   lifecycle:
     *     #默认30s #  shutdown: GRACEFUL # GRACEFUL/IMMEDIATE (默认:IMMEDIATE)
     *     timeout-per-shutdown-phase: 60s
     *
     * sringbootproject:
     * 当stop  idea 时候，后续请求还可以进来，等所有请求都完成时候，idea 自动stop
     *springboot 2.7.6
     *一个请求之后stop,后续的请求无法进来，时间到之后服务自动停止，
     *在虚拟机测试：一个请求之后kill -2 pid,后续的请求无法进来，时间到之后服务自动停止，
     *
     * win taskkill /pid /f 直接杀掉进程。不会优雅停机
     *
     *
     *关闭服务，执行kill -2或者Ctrl + C。
     * 此处执行kill -2 而不是kill -9。kill -2 相当于快捷键Ctrl + C会触发 Java 的 ShutdownHook 事件处理。
     *
     *
     *
     *
     *
     * 如果服务关闭，通过nginx 访问会报 Could not send request
     * Error: Request timed out
     *
     *在使用 kill -9 前，应该先使用 kill -15，
     *kill pid  默认 kill -15 pid. kill15不会停止子进程，kill 2会停止子进程
     * kill pid 或  kill -2 pid 关闭服务，请求的任务会继续执行直到返回，此时jps今晨还在，请他新的请求
     * 不会请求成功，之前请求结束之后，进程会结束
     *
     *
     * kill -15 发送的是 SIGTERM 信号，这是一个终止信号，用于请求进程正常退出。
     * Spring Boot 默认会捕获 SIGTERM 信号，并触发关闭流程，包括调用 @PreDestroy 注解的方法、执行 DisposableBean 接口的 destroy 方法，以及运行注册的 ShutdownHook41015。
     *
     *
     *
     *
     * windows:
     * 在 Windows 系统中，关闭文档窗口（如 Word、Excel 等）时，操作系统并不会发送类似于
     * Linux 的 kill 信号（如 SIGTERM 或 SIGKILL）应用程序可以捕获 WM_CLOSE 消息并决定如何处理：
     * 如果应用程序选择处理 WM_CLOSE，它可以执行清理操作（如保存文档、释放资源等），然后关闭窗口。
     * 如果应用程序未处理 WM_CLOSE，窗口会直接关闭。
     *
     *任务管理器会直接终止进程，类似于 Linux 的 kill -9，不会发送 WM_CLOSE 消息。
     *
     *
     *
     *
     *
     * @throws InterruptedException
     */
    @GetMapping(value = "/shutdownGracefulTest")
    public void shutdownGracefulTest() throws InterruptedException {
        log.info("before trace_shutdownGracefulTest");
        this.productTestService.updateField();
        // timeout-per-shutdown-phase: 60s
//        Thread.sleep(90 * 1000);
        log.info("after trace_shutdownGracefulTest");

    }


    @GetMapping(value = "/shutdownGracefulTest1")
    public void shutdownGracefulTest1() throws InterruptedException {
        log.info("before trace_shutdownGracefulTest1");
        Thread.sleep(50 * 1000);
        log.info("after trace_shutdownGracefulTest1");
    }

    @GetMapping(value = "/redissonTest")
    public void redissonTest() throws Exception {
        String operationLockKey = "redissonTest";
        RLock lock = redissonClient.getLock(operationLockKey);
        //不释放锁，锁会一直占用
//        lock.lock();
//        throw new Exception("dssdsd");

        try {
            lock.lock();
            throw new Exception("dssdsd");
        } finally {
            //先释放锁，然后在统一异常处理的时候 ，捕获异常
            lock.unlock();
            ;
        }


//        try {
//            lock.lock();
//            throw new Exception("dssdsd");
//        } catch (Exception ex) {
//            int m = 0;
//            throw ex;
//        } finally {
//            //先释放锁，然后在统一异常处理的时候 ，捕获异常
//            lock.unlock();
//            ;
//        }


//        try {
//
//            //会打印堆栈信息
//            //	at com.example.demo.controller.UtilityController.redissonTest(UtilityController.java:1639) ~[classes/:?]
//           int m=Integer.parseInt("ds");
//        } catch (NumberFormatException ex) {
//            //会打印原始堆栈信息
////            throw ex;
//
//            throw ex;
//        } finally {
//
//        }


    }

    @GetMapping(value = "/configurationTest")
    public String configurationTest() {
        int m = 0;
        //配置文件没有配置，就使用默认值，配置了就是用配置的值
        int age = configModelProperty.getAge();
        return configModelProperty.getAddress();
    }

    @GetMapping(value = "/mapStructTest")
    public String mapStructTest() {
        //BeanUtils 浅拷贝
//        BeanUtils.copyProperties();
//        MapStruct 默认浅拷贝 要手动写配置的东西，比较麻烦。简单的就是用spring 的 BeanUtils
        return "";
    }

    @GetMapping(value = "/newObjectCostTime")
    public void newObjectCostTime() throws Exception {
        shipOrderInfoService.newObjectCostTime();
    }


    @GetMapping(value = "/assertTest")
    public void assertTest(String text) {
        //不存在就扔异常，简化if 判断
        Assert.hasText(text, "text is empty");
    }

    @PostMapping(value = "/hashMapParam")
    public MessageResult<Void> hashMapParam(@RequestBody TestRequest request) {
        //不存在就扔异常，简化if 判断
        HashMap<String, String> map = request.getHashMap();
        return MessageResult.success();
    }

    /***
     * sleuthTraceId 链路参见spring-cloud-nacos module serviceproviderone
     * MDC TraceIdFilter
     *  sleuth 会把traceId 写入MDC ,可通过MDC获取traceId: MDC.get("traceId"),
     *
     * sleuth 实现了traceId spanId,nacos 、consumer、provider 内部的
     * 每个服务 traceId 一样 。服务间spanId 不一样。
     *
     * Trace ID是调用链的全局唯一标识符.每个服务一个spanId
     * 发生熔断之后，调用方不会收到服务方的返回消息
     *
     *
     *sleuth 不会在xxl-job中生成traceId 和spanId
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/sleuthTraceId")
    public MessageResult<Void> sleuthTraceId(HttpServletRequest httpServletRequest, TestRequest request) {
        // MDC TraceIdFilter
        //  sleuth 会把traceId 写入MDC ,可通过MDC获取traceId: MDC.get("traceId"),
        TraceContext traceContext = (TraceContext) httpServletRequest.getAttribute(TraceContext.class.getName());
        String traceId = traceContext.traceId();
        String spanId = traceContext.spanId();
        log.info("1链路跟踪测试{}", traceId);
//        MqMessage mqMessage = new MqMessage
//                (RabbitMQConfig.BATCH_DIRECT_EXCHANGE_NAME,
//                        RabbitMQConfig.BATCH_DIRECT_ROUTING_KEY,
//                        RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME,
//                        traceId);
//        directExchangeProducer.produceNotConvertSent(mqMessage);
        String mdcTraceId = MDC.get("traceId");
        log.info("2链路跟踪测试{}", traceId);
//        return MessageResult.success();


        MDCAdapter mdc = MDC.getMDCAdapter();
//        Map<String, String> map=((LogbackMDCAdapter)mdc).getPropertyMap();
//        String traceId=map.get("traceId");

        String t = TraceIdCreater.getTraceId();

        ThreadLocalRandom tr = ThreadLocalRandom.current();
        long nextLong = tr.nextLong();

        String ttt = TraceIdCreater.toLowerHex(nextLong);
        return MessageResult.success();


    }

    /**
     * 默认情况下，Spring MVC中的Controller方法是同步执行的。如果要进行异步处理，可以使用@Async注解或者通过自定义的TaskExecutor。
     *异步请求：在service 方法内部使用线程池，或在service方法上加@Async（要配置线程池 ：ThreadPoolExecutorConfig和配置类上添加@EnableAsync来启用异步支持）
     *
     *
     * 如果客户端发起 HTTP 请求，并在服务器处理过程中连接断开，Spring Boot 默认会继续执行该请求的逻辑。
     *
     * 普通 HTTP 请求通常会继续执行，即使客户端断开连接。
     * 异步任务或定时任务与客户端状态无关，仍会继续运行。
     * 在需要中止任务的场景中，建议通过客户端连接状态检测、中断标志位或 Future 的取消机制进行控制。
     * @return
     * @throws InterruptedException
     */

    @GetMapping(value = "/clientDisconnect")
    public MessageResult<Void> clientDisconnect() throws InterruptedException {


        boolean isAsyncStarted = this.httpServletRequest.isAsyncStarted();
        if (isAsyncStarted) {
            httpServletRequest.getAsyncContext().addListener(new AsyncListener() {
                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                    // 处理超时
                    log.info("onTimeout");
                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                    // 处理开始
                    log.info("onStartAsync");
                }

                @Override
                public void onError(AsyncEvent event) throws IOException {
                    // 处理错误
                    log.info("onStartAsync");
                }

                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    log.info("onComplete");
                    // 请求处理完成，可以在这里检查是否是因为客户端断开连接而完成的
                    Throwable throwable = event.getThrowable();
                    if (throwable instanceof ClientAbortException) {
                        // 客户端断开连接
//                    deferredResult.setResult("Client aborted connection");
                        log.info("ClientAbortException");
                    }
                }
            });
        }
        // 注册一个请求监听器，用于处理请求结束事件


//    连接状态  false:  HttpServletRequest 对象中的 isAsyncStarted()
        boolean started = this.httpServletRequest.isAsyncStarted();
        //模拟耗时任务
        Thread.sleep(60 * 1000);
        boolean started1 = this.httpServletRequest.isAsyncStarted();
        int n = 0;
        return MessageResult.success();
    }

    @GetMapping(value = "/deadLockOne")
    public MessageResult<Void> deadLockOne() throws InterruptedException {
        productTestService.deadLockOne();
        return MessageResult.success();
    }

    /**
     * 临间锁：降级行锁（update 当前读）,两个方法修改顺序不一致。导致两个事务内互相争取资源死锁
     * deadLockOne 先执行，成功。deadLockTwo 后执行，失败回滚。
     * mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Deadlock found when trying to get lock; try restarting transaction",
     * @throws InterruptedException
     */
    @GetMapping(value = "/deadLockTwo")
    public MessageResult<Void> deadLockTwo() throws InterruptedException {
        productTestService.deadLockTwo();
        return MessageResult.success();
    }

    @GetMapping(value = "/jacksonEmptyTest")
    public MessageResult<Void> jacksonEmptyTest() throws Exception {
        throw new Exception("");
//        return MessageResult.success();
    }

    @GetMapping(value = "/globalTest")
    public MessageResult<String> globalTest() {

//        根据code从环境中自动获取对应国际化内容
//        String msg = messageSource.getMessage("100001", null, LocaleContextHolder.getLocale());

        String msg = messageSource.getMessage("100001", new Object[]{1, 2}, LocaleContextHolder.getLocale());
        // logTest 1 2
        log.info("logTest {} {}", 1, 2);
        //SL4FJ中占位符不像MessageFormat需要指定index，而是直接使用{}即可。
        // 指定index 也不会输出参数 ：logTest1 {0} {1}
        log.info("logTest1 {0} {1}", 1, 2);
        return MessageResult.success(msg);
    }

    @GetMapping(value = "/exceptionTest")
    public MessageResult<String> exceptionTest() {
//        Throwable 类有两个重要的子类:
//        Exception :程序本身可以处理的异常，可以通过 catch 来进行捕获。Exception 又可以分为 Checked Exception (受检查异常，必须处理) 和 Unchecked Exception (不受检查异常，可以不处理)。
//        Error：Error 属于程序无法处理的错误 ，我们没办法通过 catch 来进行捕获不建议通过catch捕获 。例如 Java 虚拟机运行错误（Virtual MachineError）、虚拟机内存不够错误(OutOfMemoryError)、类定义错误（NoClassDefFoundError）等 。这些异常发生时，Java 虚拟机（
//        JVM）一般会选择线程终止。


//      RuntimeException （运行时异常）继承Exception，RuntimeException 及其子类都统称为非受检查异常
        return MessageResult.success();
    }

    @GetMapping(value = "/eventBusTest")
    public MessageResult<String> eventBusTest() {
        productTestService.eventBusTest();
        return MessageResult.success();
    }

    @GetMapping(value = "/camundaRestTestOne")
    public MessageResult<Person> camundaRestTestOne() {
        Person person = new Person();
        person.setAge(15);
        person.setName("fancky");
        return MessageResult.success(person);
    }

    @GetMapping(value = "/camundaRestTestTwo")
    public MessageResult<Person> camundaRestTestTwo(Person dto) {
        Person person = new Person();
        person.setAge(16);
        person.setName("li");
        return MessageResult.faile(person);
    }

    @PostMapping(value = "/camundaRestTestThree")
    public MessageResult<Person> camundaRestTestThree(@RequestBody Person dto) {
        Person person = new Person();
        person.setAge(17);
        person.setName("ming");
        return MessageResult.faile(person);
    }

    /**
     * 事务重复读取:同一个事务内重复读取
     * @return
     * @throws InterruptedException
     */
    @GetMapping(value = "/repeatReadTest")
    public MessageResult<Void> repeatReadTest() throws InterruptedException {
        productTestService.repeatReadTest();
        return MessageResult.success();
    }

    /**
     * 事务重复读取:一个事务两次读取另一个事务期间修改了数据
     *
     * MyBatis 缓存 vs 事务隔离：MyBatis 的一级缓存可能会影响你观察到的事务隔离行为，特别是在同一事务中多次执行相同查询时
     * @return
     * @throws InterruptedException
     */
    @GetMapping(value = "/repeatReadMultiTransaction")
    public MessageResult<Void> repeatReadMultiTransaction() throws InterruptedException {
        productTestService.repeatReadMultiTransaction();
        return MessageResult.success();
    }


    @GetMapping(value = "/repeatReadMultiTransactionUpdate")
    public MessageResult<Void> repeatReadMultiTransactionUpdate() throws InterruptedException {
        productTestService.repeatReadMultiTransactionUpdate();
        return MessageResult.success();
    }


    @GetMapping(value = "/transactionalFunTest")
    public MessageResult<Void> transactionalFunTest() {
        productTestService.transactionalFunTest();
        return MessageResult.success();
    }

    @GetMapping(value = "/batchUpdateByCondition")
    public MessageResult<Void> batchUpdateByCondition() {
        productTestService.batchUpdateByCondition();
        return MessageResult.success();
    }


    @GetMapping(value = "/batchUpdateBySelective")
    public MessageResult<Void> batchUpdateBySelective() throws Exception {
        productTestService.batchUpdateBySelective();
        return MessageResult.success();
    }


    @GetMapping(value = "/refreshScopeTest")
    public MessageResult<String> refreshScopeTest() throws Exception {
        return MessageResult.success(configModelProperty.getFistName());
    }

    @GetMapping(value = "/transactionalCallBack")
    public MessageResult<String> transactionalCallBack() throws Exception {

        productTestService.transactionalCallBack();
        return MessageResult.success();
    }

    @GetMapping(value = "/mqMessageTest")
    public MessageResult<String> mqMessageTest() throws Exception {
        MqMessage mqMessage = mqMessageService.getById(85);
        mqMessageService.update(mqMessage);
        return MessageResult.success();
    }

    @GetMapping(value = "/mqMessageList")
    public MessageResult<PageData<MqMessageResponse>> mqMessageList(MqMessageRequest request) throws Exception {

//        list转map   当尝试获取一个不存在的键时，会返回null
//        Map<Integer, MqMessage> locationDOMap = list.stream().collect(Collectors.toMap(MqMessage::getId, item -> item));
        PageData<MqMessageResponse> pageData = mqMessageService.list(request);
        return MessageResult.success(pageData);
    }

    /**
     * postman 启动两个tab 请求这个url,模拟并发
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/threadLocalTest")
    public MessageResult<String> threadLocalTest() throws Exception {
        String traceId = TraceIdHolder.getTraceId();
        log.info("Before sleep traceId - {}", traceId);
        Thread.sleep(30000);
        log.info("TraceId - {}", TraceIdHolder.getTraceId());
        return MessageResult.success();
    }


    /**
     * OpenFeign 调用
     * @param token
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/completeShipOrder")
    public MessageResult<String> completeShipOrder(@RequestHeader("Authorization") String token) throws Exception {

//        for (int i = 0; i < 10; i++) {
//            BigInteger shipOrderId = new BigInteger("656835527819333");
//            String token1 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkOWEyMTliOS1hMzU4LWY4NmYtYjAzYi0zYTBlZGE1Y2RhYjciLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImQ5YTIxOWI5LWEzNTgtZjg2Zi1iMDNiLTNhMGVkYTVjZGFiNyIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QwMDIiLCJnaXZlbl9uYW1lIjoidGVzdDAwMiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6ImFkbWluaXN0cmF0b3IiLCJyb2xlIjoiYWRtaW5pc3RyYXRvciIsIm5iZiI6MTc0MjQ1OTk3NywiZXhwIjoxNzQyNTQ2Mzc3LCJpc3MiOiJBdXRob3JpemVTU08iLCJhdWQiOiJBdXRob3JpemVTU08ifQ.zNjpID4o_V4_RFmOhFMtNKV6iT2Cit8f6_iB4YWrnQM";
//            String re = wmsService.completeShipOrder(shipOrderId, token);
//            log.info(re);
//        }

        for (int i = 0; i < 10; i++) {

            BigInteger shipOrderId = new BigInteger("656835527819333");
            String token1 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkOWEyMTliOS1hMzU4LWY4NmYtYjAzYi0zYTBlZGE1Y2RhYjciLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImQ5YTIxOWI5LWEzNTgtZjg2Zi1iMDNiLTNhMGVkYTVjZGFiNyIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QwMDIiLCJnaXZlbl9uYW1lIjoidGVzdDAwMiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6ImFkbWluaXN0cmF0b3IiLCJyb2xlIjoiYWRtaW5pc3RyYXRvciIsIm5iZiI6MTc0MjQ1OTk3NywiZXhwIjoxNzQyNTQ2Mzc3LCJpc3MiOiJBdXRob3JpemVTU08iLCJhdWQiOiJBdXRob3JpemVTU08ifQ.zNjpID4o_V4_RFmOhFMtNKV6iT2Cit8f6_iB4YWrnQM";
            threadPoolExecutor.execute(() -> {
                String re = wmsService.completeShipOrder(shipOrderId, token1);
                log.info(re);
            });

        }
        return MessageResult.success();
    }


    @GetMapping(value = "/pointcutExecuteOrder")
    public MessageResult<String> pointcutExecuteOrder() {
        productTestService.pointcutExecuteOrder();
        return MessageResult.success();
    }


    /**
     *  redissonLock 可重入
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/redissonLockReentrantLock")
    public MessageResult<String> redissonLockReentrantLock() throws Exception {
        mqMessageService.redissonLockReentrantLock();
        return MessageResult.success();
    }

    /**
     * redissonLock 锁释放事务未提交
     *  解决并发下 redissonLock 释放了 事务未提交
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/redissonLockReleaseTransactionalUnCommit")
    public MessageResult<String> redissonLockReleaseTransactionalUnCommit() throws Exception {

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() -> {
                try {
                    final int j = finalI;
                    mqMessageService.redissonLockReleaseTransactionalUnCommit(j);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return MessageResult.success();
    }

    /**
     *主线程可以打印出异常堆栈信息
     *
     * 主线程无法捕捉子线程的异常信息，子线程要处理异常信息
     * @return
     */
    @GetMapping(value = "/tryThrowStackTrace")
    public MessageResult<String> tryThrowStackTrace() {
        try {
            productTestService.tryThrowStackTrace();
        } catch (Exception e) {
            throw e;
        }
        return MessageResult.success();
    }

    @GetMapping(value = "/shipOrderTest")
    public MessageResult<String> shipOrderTest() {
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() -> {
                final int j = finalI;
                wmsService.shipOrderTest(j + "");
            });
        }
        return MessageResult.success();
    }

    @GetMapping(value = "/checkRelation")
    public MessageResult<String> checkRelation(ShipOrderInventoryDetailDto dto, @RequestHeader("token") String token) {
        wmsService.checkRelation(dto,token);
        return MessageResult.success();
    }

}

