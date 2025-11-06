package com.example.demo.aop.aspect;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.utility.RedisKeyConfigConst;
import com.example.demo.utility.RepeatPermission;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*


<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>

切点表达式:参考https://www.cnblogs.com/zhangxufeng/p/9160869.html
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
modifiers-pattern：方法的可见性，如public，protected；
ret-type-pattern：方法的返回值类型，如int，void等；
declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
name-pattern：方法名类型，如businessService()；
param-pattern：方法的参数类型，如java.lang.String；
throws-pattern：方法抛出的异常类型，如java.lang.Exception；

实例：
execution(public * com.spring.service.BusinessObject.businessService(java.lang.String,..))
匹配使用public修饰，返回值为任意类型，并且是com.spring.BusinessObject类中名称为businessService的方法，
方法可以有多个参数，但是第一个参数必须是java.lang.String类型的方法


//日志采用ControllerAdvice还是Aspect

Web层日志（URL、HTTP方法、参数等） → 使用 @ControllerAdvice
业务方法日志（Service层方法调用、执行时间等） → 使用 AOP Aspect
异常日志 → 两者结合：@ControllerAdvice 处理HTTP异常，Aspect 处理业务异常
安全审计日志 → 优先使用 AOP Aspect（可以精确到方法级别）




 *通配符：该通配符主要用于匹配单个单词，或者是以某个词为前缀或后缀的单词。
..通配符：该通配符表示0个或多个项，主要用于declaring-type-pattern和param-pattern中，如果用于declaring-type-pattern中，
          则表示匹配当前包及其子包，如果用于param-pattern中，则表示匹配0个或多个参数。
 */
@Aspect
@Component
@Order(101)//数值越小，优先级越高 如果不在配置类中指定bean注册顺序，就在Aspect加 @Order(202) 注解指定顺序
//@Slf4j
@Log4j2
public class LogAspect {


    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ObjectMapper objectMapper;


    /*
    注解@Slf4j的使用
    声明:如果不想每次都写private  final Logger logger = LoggerFactory.getLogger(当前类名.class); 可以用注解@Slf4j;
    在pom文件加入lombok的依赖



//    注解：@Data
//    @Data也是lombok提供的，免去了实体类中getter和setter方法，代码更简洁，编译的时候会自动生成getter和setter方法：
//    */
//
//
//    /**
//     * 目标方法执行之前执行
//     * 如果异常了将不会执行切点方法
//     *
//     * @param jp
//     */
//    @Before("execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))")
//    // 所有controller包下面的所有方法的所有参数
//    public void beforeMethod(JoinPoint jp) {
//
//        String methodName = jp.getSignature().getName();
//        //切点方法所在的类
//        Object obj1 = jp.getTarget();
//        //切点方法的参数
//        Object obj2 = jp.getArgs();
//        if (obj2 instanceof Student) {
//            Users user = (Users) obj2;
//            int m = 0;
//        }
//        Class cla = obj2.getClass();
//        Object ob = jp.getThis();
//
//        //获取不到MethodInvocationProceedingJoinPoint内部私有成员信息。
//        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = null;
//        if (jp instanceof MethodInvocationProceedingJoinPoint) {
//            methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint) jp;
//            Object o = methodInvocationProceedingJoinPoint.getTarget();
//            Object o1 = methodInvocationProceedingJoinPoint.getThis();
//        }
//
//
//        log.info("【前置增强】the method 【" + methodName + "】 begins with " + JSON.toJSONString(jp.getArgs()));
//    }
//
//    /**
//     * 后置增强：目标方法执行之后执行以下方法体的内容，不管是否发生异常。
//     *
//     * @param jp
//     */
//    @After("execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))")
//    public void afterMethod(JoinPoint jp) {
//        log.info("【后置增强】this is a afterMethod advice...");
//    }
//
//    /**
//     * 返回增强：目标方法正常执行完毕时执行
//     *
//     * @param jp
//     * @param result 返回值
//     */
//    @AfterReturning(value = "execution(* com.example.demo.controller.UtilityController.addStudentAspect(..)))", returning = "result")
//    public void afterReturningMethod(JoinPoint jp, Object result) {
//        String methodName = jp.getSignature().getName();
//        log.info("【返回增强】the method 【" + methodName + "】 ends with 【" + result + "】");
//    }
//
//    /**
//     * 异常增强：目标方法发生异常的时候执行，第二个参数表示补货异常的类型
//     *
//     * @param jp
//     * @param e
//     */
//    @AfterThrowing(value = "execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))", throwing = "e")
//    public void afterThorwingMethod(JoinPoint jp, Exception e) {
//        String methodName = jp.getSignature().getName();
//        log.error("【异常增强】the method 【" + methodName + "】 occurs exception: ", e);
//    }

//配置过个路径
//    @Around(value = "execution(* com.fnd.businessvehicleintelligent.*.controller.*.*(..))" +
//            "||execution(* com.fnd.mq.controller.*.*(..))")

    @Pointcut("execution(* com.example.demo.controller.*.*(..))")
    public void pointCut() {
    }

    //region  redissonClient version
//
//    /**
//     * 环绕增强：目标方法执行前后分别执行一些代码，发生异常的时候执行另外一些代码
//     *
//     * @return
//     */
////    @Around(value = "execution(* com.example.demo.controller.*.*(..))")
//    @Around(value = "pointCut()")
//    public Object aroundMethod(ProceedingJoinPoint jp) throws Throwable {
//
//
//        String httpMethod = httpServletRequest.getMethod();
//        switch (httpMethod) {
//            case "POST":
//                break;
//            case "DELETE":
//                break;
//            case "PUT":
//                break;
//            case "GET":
//                break;
//            default:
//                break;
//        }
//        String methodName = jp.getSignature().getName();
//        //获取方法
//        Signature signature = jp.getSignature();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        Method method = methodSignature.getMethod();
//        //方法参数
//        Object[] args = jp.getArgs();
//
//
//        String className = jp.getTarget().getClass().toString();
////        String methodName = jp.getSignature().getName();
////        Object[] args = jp.getArgs();
////
////
//        log.info("{} - {} 开始处理,参数列表 - {}", className, methodName, Arrays.toString(args));
////        Object result = jp.proceed();
////        log.info("{} - {} 处理完成,返回结果 - {}", className, methodName,objectMapper.writeValueAsString(result));
////
//
//
//        RepeatPermission repeatPermission = method.getDeclaredAnnotation(RepeatPermission.class);
//        MessageResult<Object> messageResult = new MessageResult<>();
//        if (repeatPermission != null) {
//
//            String repeatToken = httpServletRequest.getHeader("repeat_token");
//            if (StringUtils.isEmpty(repeatToken)) {
//                // 抛出让ControllerAdvice全局异常处理
//                throw new Exception("can not find token!");
//            }
//            /**
//             * 注解代码浸入太大，
//             * 1、唯一索引，
//             * 2、（1）前台打开新增页面访问后台获取该表的token (存储在redis 中的uuid)key:用户id_功能.value token
//             *        获取token时候判断用户有没有没有过期时间的token，有就说明已请求，直接返回
//             *   （2） 检测前段提交的token是不是在redis 中而且过期时间不为0，验证通过入库成功更新redis 中的token过期时间
//             * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
//             *
//             */
//            //重复提交：redis 中设置带有过期的key,判断是否存在。  过期防止程序异常，不释放锁
//            //在redis中判断 userid + path 是否存在
//
//            //redis 中设置key
//
//            BigInteger userId = new BigInteger("1");
//            String uri = httpServletRequest.getRequestURI();
//            String key = "repeat:" + uri + "_" + userId.toString();
//
//            RLock lock = redissonClient.getLock(key);
//
//            try {
//                boolean isLocked = lock.isLocked();
//                if (isLocked) {
//                    //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
//                    messageResult.setSuccess(false);
//                    messageResult.setMessage("重复提交：服务器繁忙");
//                    return messageResult;
//                }
//                //tryLock(long waitTime, long leaseTime, TimeUnit unit)
//                long waitTime = 1;//获取锁等待时间
//                long leaseTime = 30;//持有所超时释放锁时间  24 * 60 * 60;
//                boolean lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
//                isLocked = lock.isLocked();
//                if (isLocked) {
//                    return jp.proceed();
//                } else {
//                    //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
//                    messageResult.setSuccess(false);
//                    messageResult.setMessage("重复提交:获取锁失败");
//                    return messageResult;
//                }
//            } catch (InterruptedException e) {
//                messageResult.setSuccess(false);
//                messageResult.setMessage(e.getMessage());
//                return messageResult;
//            } finally {
//                //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
//                //unlock 删除key
//                lock.unlock();
//
//            }
//        } else {
//            StopWatch stopWatch = new StopWatch("");
//            stopWatch.start("");
//            Object obj = jp.proceed();
//            stopWatch.stop();
//            long costTime = stopWatch.getTotalTimeMillis();
//            log.info("{} - {} 处理完成,耗时 {} ms ,返回结果 - {} ", className, methodName, costTime, objectMapper.writeValueAsString(messageResult));
//            return obj;
//            // return jp.proceed();
//        }
//
//
//    }
//endregion


    //region redis token version

    /**
     * 环绕增强：目标方法执行前后分别执行一些代码，发生异常的时候执行另外一些代码
     *
     * @return
     */
//    @Around(value = "execution(* com.example.demo.controller.*.*(..))")
    @Around(value = "pointCut()")
    public Object aroundMethod(ProceedingJoinPoint jp) throws Throwable {

        StopWatch stopWatch = new StopWatch("");
        stopWatch.start("");
        String httpMethod = httpServletRequest.getMethod();
        ///sbp/demo/demoProductTest
        String uri = httpServletRequest.getRequestURI();
        // /sbp
        String contextPath = httpServletRequest.getContextPath();
        ///demo/demoProductTest
        String servletPath = httpServletRequest.getServletPath();
        switch (httpMethod) {
            case "POST":
                break;
            case "DELETE":
                break;
            case "PUT":
                break;
            case "GET":
                break;
            default:
                break;
        }
        String methodName = jp.getSignature().getName();
        //获取方法
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //方法参数
        Object[] args = jp.getArgs();
        String className = jp.getTarget().getClass().toString();

//        log.info("{} : {} - {} 开始处理,参数列表 - {}", uri, className, methodName, Arrays.toString(args));

        // 处理参数序列化，特别处理文件上传情况
        List<Object> loggableArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                loggableArgs.add("File[" + ((MultipartFile) arg).getOriginalFilename() + "]");
            } else if (arg instanceof MultipartFile[]) {
                loggableArgs.add("Files[" + Arrays.stream((MultipartFile[]) arg)
                        .map(MultipartFile::getOriginalFilename)
                        .collect(Collectors.joining(",")) + "]");
            } else if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                // 忽略这些参数
            } else {
                loggableArgs.add(arg);
            }
        }

        String argsJson = objectMapper.writeValueAsString(loggableArgs);
        log.info("{} : {} - {} 开始处理,参数列表 - {}", uri, className, methodName, argsJson);






//        String operationLockKey = keyWithOutToken + RedisKeyConfigConst.KEY_LOCK_SUFFIX;
        RepeatPermission repeatPermission = method.getDeclaredAnnotation(RepeatPermission.class);
        Object result = null;
        if (repeatPermission != null) {
            //      d/sbp/demo/demoProductTest
//            String uri = httpServletRequest.getRequestURI();
            String apiName = repeatPermission.value();
            if (StringUtils.isEmpty(apiName)) {
                apiName = method.getName();
            }
            String repeatToken = httpServletRequest.getHeader("repeat_token");
            if (StringUtils.isEmpty(repeatToken)) {
                // 抛出让ControllerAdvice全局异常处理
                throw new Exception("can not find token!");
            }
            /**
             * 一锁二判三更新四数据库兜底 唯一约束
             * 一、加锁。系统只有一个地方更新。多个地方更新，可设计第三步条件更新
             * 二、状态字段，判断是否更新过。如：版本号，状态字段。状态机、流水表、唯一性索引
             * 三、更新数据库
             *
             * 数据库设计兜底措施：唯一索引，流水表
             *
             *
             * rpc ：request消息中加requestId或者把请求参数md5摘要成字符串
             * 方案一
             * 1、唯一索引，
             * 2、（1）前台打开新增页面访问后台获取该表的token (存储在redis 中的uuid)key:用户id_功能.value token
             *        获取token时候判断用户有没有没有过期时间的token，有就说明已请求，直接返回
             *         UtilityController  getRepeatToken
             *   （2） 检测前段提交的token是不是在redis 中而且过期时间不为0，验证通过更新redis 中的token过期时间
             * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
             *
             *
             *
             *方案二
             *设计思路新加一个处理结果状态，
             *
             * 1、唯一索引，
             *
             * token 对象{tokenStr uuid,state枚举值：0 未处理 1：处理中 2：处理完成 }
             * 2、（1）前台打开新增页面访问后台获取该表的token 对象key:用户id_功能.value token 对象
             *       并将token 对象存入数据库
             *   （2） 检测前段提交时候，获取token时候判断是不是在redis
             *         a:token 不存在返回。
             *         b:token 存在，判断token对象的状态是不是0，0说明没请求，否则直接返回
             *         验证通过入库成功更新redis的状态值1和token过期时间，事务成功更新token的状态值2
             *
             *
             *
             * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
             */
            //重复提交：redis 中设置带有过期的key,判断是否存在。  过期防止程序异常，不释放锁
            //在redis中判断 userid + path 是否存在

            //redis 中设置key
            BigInteger userId = new BigInteger("1");
//            String uri = httpServletRequest.getRequestURI();
            String key = "repeat:" + userId + ":" + apiName;
//            String keyWithOutToken = "repeat:" + userId + ":" + uri;
//            String key = "repeat:" + userId + ":" + repeatToken;
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            try {
                //UtilityController getRepeatToken 时候向redis 插入一个token
                //添加重复消费redis 校验，不会存在并发同一个message
                Object tokenObj = valueOperations.get(key);
                if (tokenObj == null) {
                    return MessageResult.faile("token is not exist!");
                }
                if (!repeatToken.equals(tokenObj.toString())) {
                    return MessageResult.faile("token is incorrect!");
                }
                Long expireTime = redisTemplate.getExpire(key);
                //有过期时间
                if (expireTime != null && !expireTime.equals(-1L)) {
                    return MessageResult.faile("repeat commit,please get token first!");
                }
                //设置过期时间：此处要加锁，防止并发（两个线程同时访问过期时间都为-1）
                //加锁可以设置redisson 或者jdk synchronized  ReentrantLock cas
                //单机使用cas AtomicInteger 性能好点。 web 服务考虑可拓展 使用分布式锁
//                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                //先设置redis 过期，然后调用业务，业务异常就重新调用key,也就浪费一个key
                //否则设计 异常的时候要把过期时间设置为-1，建议采用上面毕竟已经处理过，尽管异常了


                String operationLockKey = key + RedisKeyConfigConst.KEY_LOCK_SUFFIX;
                //并发访问，加锁控制
                //wms_es设计token和指纹两种
                //UtilityController getRepeatToken 时候向redis 插入一个token
                //redis token 状态删除设置过期时间操作失败，可设计token 存储在数据库和业务数据做原子操作。在业务层做判断tokens是否处理
                RLock lock = redissonClient.getLock(operationLockKey);
                boolean lockSuccessfully =false;
                try {
                    //tryLock(long waitTime, long leaseTime, TimeUnit unit)
                    //获取锁等待时间
                    long waitTime = 10;
                    //持有所超时释放锁时间  24 * 60 * 60;
                    // 注意：锁超时自动释放，另外一个线程就会获取锁继续执行，代码版本号处理
                    long leaseTime = 30;
                    //不指定leaseTime看门狗自动续期
                    lock.lock();
                    //此处最好用lock.lock();避免业务执行期间释放了锁。配合限流使用。\
//                    默认30秒leaseTime，但看门狗会每10秒检查并续期
//                    只要线程存活且业务未完成，锁会一直持有
//                    业务完成后必须手动unlock()
                    //不设置 releaseTime（启用看门狗）
//                    lock.lock(leaseTime, TimeUnit.SECONDS);
                     lockSuccessfully = lock.tryLock(waitTime, TimeUnit.SECONDS);

//                    boolean lockSuccessfully = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
                    if (lockSuccessfully) {
                        //获取锁之后判断过期时间是否被之前线程设置过，设置过就处理过业务
                        expireTime = redisTemplate.getExpire(key);
                        //有过期时间
                        if (expireTime != null && !expireTime.equals(-1L)) {
                            return MessageResult.faile("repeat commit,please get token first!");
                        }

//                        Object obj = monitor(jp, servletPath);
//                        //业务处理成功再设计才设置过期时间
                        redisTemplate.expire(key, 1, TimeUnit.DAYS);
//                        return obj;
                    } else {
                        //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                        //超过waitTime ，扔未获得锁
                        return MessageResult.faile("重复提交:获取锁失败");
                    }
                } catch (InterruptedException e) {
                    //走全局异常处理
                     throw  e;
//                    return MessageResult.faile(e.getMessage());
                } finally {
                    //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
                    //unlock 删除key
                    if (lockSuccessfully && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
                result = monitor(jp, servletPath);
            } catch (Exception e) {
                //redis 保证高可用
                // redisTemplate.delete(key);
                return MessageResult.faile(e.getMessage());
            }

        } else {
            result = monitor(jp, servletPath);
        }
        stopWatch.stop();
        long costTime = stopWatch.getTotalTimeMillis();
//        如果是列别插叙数据量大，会影响性能
        // log.debug("{} : {} - {} 处理完成,返回结果 - {}", uri, className, methodName, objectMapper.writeValueAsString(result));
        log.debug("aroundMethod cost_time {} ms {} : {} - {} 处理完成,返回结果 - {}", costTime, uri, className, methodName, objectMapper.writeValueAsString(result));

        return result;

    }

    private Object monitor(ProceedingJoinPoint jp, String servletPath) throws Throwable {
        StopWatch stopWatch = new StopWatch("");
        stopWatch.start("");
        Object obj = jp.proceed();
        //proceed 执行完，包括事务回调执行完 才会执行到此
        stopWatch.stop();
        long costTime = stopWatch.getTotalTimeMillis();
        MessageResult<Object> messageResult = MessageResult.success(obj);
        log.info("{} 处理完成,cost_time {} ms ,返回结果 - {} ", servletPath, costTime, objectMapper.writeValueAsString(messageResult));
        return obj;
    }
    //endregion

    //        @AfterThrowing(pointcut = "execution(* com.example.demo.controller.*.*(..))", throwing = "ex")
    @AfterThrowing(pointcut = "pointCut()", throwing = "ex")
    public void onExceptionThrow(Exception ex) {
        log.error("", ex);
    }


    private void logRequestInfo(HttpServletRequest request, ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== 请求信息 ===\n");
        sb.append("URL: ").append(request.getRequestURL()).append("\n");
        sb.append("Method: ").append(request.getMethod()).append("\n");
        sb.append("Content-Type: ").append(request.getContentType()).append("\n");

        // 获取GET参数
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            sb.append("Parameters:\n");
            paramMap.forEach((key, values) ->
                    sb.append("  ").append(key).append(": ").append(Arrays.toString(values)).append("\n"));
        }

        // 获取POST请求体
        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getContentType() != null
                && request.getContentType().contains("application/json")) {
            try {
                String body = request.getReader().lines().collect(Collectors.joining());
                sb.append("Body: ").append(body).append("\n");
            } catch (IOException e) {
                log.error("读取请求体失败", e);
            }
        }

        // 打印方法参数
        sb.append("Method Args:\n");
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            sb.append("  ").append(parameters[i].getName()).append(": ").append(args[i]).append("\n");
        }

        log.info(sb.toString());
    }
}
