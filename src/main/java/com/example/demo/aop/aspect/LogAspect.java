package com.example.demo.aop.aspect;

import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.utility.RepeatPermission;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
name-pattern：方法名类型，如buisinessService()；
param-pattern：方法的参数类型，如java.lang.String；
throws-pattern：方法抛出的异常类型，如java.lang.Exception；

实例：
execution(public * com.spring.service.BusinessObject.businessService(java.lang.String,..))
匹配使用public修饰，返回值为任意类型，并且是com.spring.BusinessObject类中名称为businessService的方法，
方法可以有多个参数，但是第一个参数必须是java.lang.String类型的方法


//日志采用ControllerAdvice,不采用Aspect

 *通配符：该通配符主要用于匹配单个单词，或者是以某个词为前缀或后缀的单词。
..通配符：该通配符表示0个或多个项，主要用于declaring-type-pattern和param-pattern中，如果用于declaring-type-pattern中，
          则表示匹配当前包及其子包，如果用于param-pattern中，则表示匹配0个或多个参数。
 */
@Aspect
@Component
@Order(101)
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


        String httpMethod = httpServletRequest.getMethod();
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
//        String methodName = jp.getSignature().getName();
//        Object[] args = jp.getArgs();
//
//
        log.info("{} - {} 开始处理,参数列表 - {}", className, methodName, Arrays.toString(args));
//        Object result = jp.proceed();
//        log.info("{} - {} 处理完成,返回结果 - {}", className, methodName,objectMapper.writeValueAsString(result));
//


        RepeatPermission repeatPermission = method.getDeclaredAnnotation(RepeatPermission.class);

        if (repeatPermission != null) {
            String apiName= repeatPermission.value();
            if (StringUtils.isEmpty(apiName)) {
                apiName=method.getName();
            }
            String repeatToken = httpServletRequest.getHeader("repeat_token");
            if (StringUtils.isEmpty(repeatToken)) {
                // 抛出让ControllerAdvice全局异常处理
                throw new Exception("can not find token!");
            }
            /**
             * 注解代码浸入太大，
             * 1、唯一索引，
             * 2、（1）前台打开新增页面访问后台获取该表的token (存储在redis 中的uuid)key:用户id_功能.value token
             *        获取token时候判断用户有没有没有过期时间的token，有就说明已请求，直接返回
             *   （2） 检测前段提交的token是不是在redis 中而且过期时间不为0，验证通过入库成功更新redis 中的token过期时间
             * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
             *
             */
            //重复提交：redis 中设置带有过期的key,判断是否存在。  过期防止程序异常，不释放锁
            //在redis中判断 userid + path 是否存在

            //redis 中设置key
            BigInteger userId = new BigInteger("1");
            String uri = httpServletRequest.getRequestURI();
            String key = "repeat:" + userId + ":" + apiName;
//            String key = "repeat:" + userId + ":" + repeatToken;
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            try {
                //getRepeatToken 时候向redis 插入一个token
                //添加重复消费redis 校验，不会存在并发同一个message
                Object tokenObj = valueOperations.get(key);
                if (tokenObj == null) {
                    return MessageResult.faile("token is not exist!");
                }
                Long expireTime = redisTemplate.getExpire(key);
                //有过期时间
                if (expireTime != null && !expireTime.equals(-1L)) {
                    return MessageResult.faile("repeat commit,please get token first!");
                }
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
                //先设置redis 过期，然后调用业务，业务异常就重新调用key,也就浪费一个key
                Object obj= monitor(jp, className, methodName);

                return obj;
            } catch (Exception e) {
                //redis 保证高可用
                // redisTemplate.delete(key);
                return MessageResult.faile(e.getMessage());
            }

        } else {
            return monitor(jp, className, methodName);
        }


    }

    private Object monitor(ProceedingJoinPoint jp, String className, String methodName) throws Throwable {
        StopWatch stopWatch = new StopWatch("");
        stopWatch.start("");
        Object obj = jp.proceed();
        stopWatch.stop();
        long costTime = stopWatch.getTotalTimeMillis();
        MessageResult<Object> messageResult = MessageResult.success(obj);
        log.info("{} - {} 处理完成,耗时 {} ms ,返回结果 - {} ", className, methodName, costTime, objectMapper.writeValueAsString(messageResult));
        return obj;
    }
    //endregion

    //        @AfterThrowing(pointcut = "execution(* com.example.demo.controller.*.*(..))", throwing = "ex")
    @AfterThrowing(pointcut = "pointCut()", throwing = "ex")
    public void onExceptionThrow(Exception ex) {
        log.error("", ex);
    }

}
