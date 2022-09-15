package com.example.demo.aspect;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.utility.RepeatPermission;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import com.example.demo.model.pojo.Student;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/*
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




 *通配符：该通配符主要用于匹配单个单词，或者是以某个词为前缀或后缀的单词。
..通配符：该通配符表示0个或多个项，主要用于declaring-type-pattern和param-pattern中，如果用于declaring-type-pattern中，
          则表示匹配当前包及其子包，如果用于param-pattern中，则表示匹配0个或多个参数。
 */
@Aspect
@Component
//@Slf4j
@Log4j2
public class LogAspect {


    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

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

    /**
     * 环绕增强：目标方法执行前后分别执行一些代码，发生异常的时候执行另外一些代码
     *
     * @return
     */
    @Around(value = "execution(* com.example.demo.controller.*.*(..))")
    public Object aroundMethod(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        //获取方法
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //方法参数
        Object[] args = jp.getArgs();


        MessageResult<Object> returnResult = null;
        Supplier<MessageResult<Object>> supplier = () ->
        {
            MessageResult<Object> messageResult = new MessageResult<>();
            try {
                log.info("【环绕增强中的--->前置增强】：the method 【" + methodName + "】 begins with " + Arrays.asList(jp.getArgs()));
                //执行目标方法
                Object result = jp.proceed();
                messageResult.setData(result);
                messageResult.setSuccess(true);
                log.info("【环绕增强中的--->返回增强】：the method 【" + methodName + "】 ends with " + result);
            } catch (Throwable e) {
                messageResult.setSuccess(false);
                messageResult.setMessage(e.getMessage());
                log.info("【环绕增强中的--->异常增强】：the method 【" + methodName + "】 occurs exception " + e);

            }
            log.info("【环绕增强中的--->后置增强】：-----------------end.----------------------");
            return messageResult;
        };

//        supplier.get();


        RepeatPermission repeatPermission = method.getDeclaredAnnotation(RepeatPermission.class);
        MessageResult<Object> messageResult = new MessageResult<>();
        if (repeatPermission != null) {


            //重复提交：redis 中设置带有过期的key,判断是否存在。  过期防止程序异常，不释放锁
            //在redis中判断 userid + path 是否存在

            //redis 中设置key

            BigInteger userId = new BigInteger("1");
            String uri = httpServletRequest.getRequestURI();
            String key ="repeat:"+ uri + "_" + userId.toString();

            RLock lock = redissonClient.getLock(key);
            try {
                boolean isLocked = lock.isLocked();
                if (isLocked) {
                    //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                    messageResult.setSuccess(false);
                    messageResult.setMessage("重复提交：服务器繁忙");
                    return messageResult;
                }
                boolean lockSuccessfully = lock.tryLock(1, 30, TimeUnit.SECONDS);
                isLocked = lock.isLocked();
                if (isLocked) {
                    return supplier.get();
                } else {
                    //如果controller是void 返回类型，此处返回 MessageResult<Void>  也不会返回给前段
                    messageResult.setSuccess(false);
                    messageResult.setMessage("重复提交:获取锁失败");
                    return messageResult;
                }
            } catch (InterruptedException e) {
                messageResult.setSuccess(false);
                messageResult.setMessage(e.getMessage());
            } finally {
                //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
                //unlock 删除key
                lock.unlock();

            }

            return messageResult;
        } else {
            return supplier.get();
        }


    }




    @AfterThrowing(pointcut = "execution(* com.example.demo.controller.*.*(..))", throwing = "ex")
    public void onExceptionThrow(Exception ex) {
        log.info("", ex);
    }

}
