package com.example.demo.aspect;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.entity.rabc.Users;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.stereotype.Component;
import com.example.demo.model.pojo.Student;

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

    /*
    注解@Slf4j的使用
    声明:如果不想每次都写private  final Logger logger = LoggerFactory.getLogger(当前类名.class); 可以用注解@Slf4j;
    在pom文件加入lombok的依赖



    注解：@Data
    @Data也是lombok提供的，免去了实体类中getter和setter方法，代码更简洁，编译的时候会自动生成getter和setter方法：
    */


    /**
     * 目标方法执行之前执行
     * 如果异常了将不会执行切点方法
     *
     * @param jp
     */
    @Before("execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))")
    // 所有controller包下面的所有方法的所有参数
    public void beforeMethod(JoinPoint jp) {

        String methodName = jp.getSignature().getName();
        //切点方法所在的类
        Object obj1 = jp.getTarget();
        //切点方法的参数
        Object obj2 = jp.getArgs();
        if (obj2 instanceof Student) {
            Users user = (Users) obj2;
            int m = 0;
        }
        Class cla = obj2.getClass();
        Object ob = jp.getThis();

        //获取不到MethodInvocationProceedingJoinPoint内部私有成员信息。
        MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = null;
        if (jp instanceof MethodInvocationProceedingJoinPoint) {
            methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint) jp;
            Object o = methodInvocationProceedingJoinPoint.getTarget();
            Object o1 = methodInvocationProceedingJoinPoint.getThis();
        }


        log.info("【前置增强】the method 【" + methodName + "】 begins with " + JSON.toJSONString(jp.getArgs()));
    }

    /**
     * 后置增强：目标方法执行之后执行以下方法体的内容，不管是否发生异常。
     *
     * @param jp
     */
    @After("execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))")
    public void afterMethod(JoinPoint jp) {
        log.info("【后置增强】this is a afterMethod advice...");
    }

    /**
     * 返回增强：目标方法正常执行完毕时执行
     *
     * @param jp
     * @param result 返回值
     */
    @AfterReturning(value = "execution(* com.example.demo.controller.UtilityController.addStudentAspect(..)))", returning = "result")
    public void afterReturningMethod(JoinPoint jp, Object result) {
        String methodName = jp.getSignature().getName();
        log.info("【返回增强】the method 【" + methodName + "】 ends with 【" + result + "】");
    }

    /**
     * 异常增强：目标方法发生异常的时候执行，第二个参数表示补货异常的类型
     *
     * @param jp
     * @param e
     */
    @AfterThrowing(value = "execution(* com.example.demo.controller.UtilityController.addStudentAspect(..))", throwing = "e")
    public void afterThorwingMethod(JoinPoint jp, Exception e) {
        String methodName = jp.getSignature().getName();
        log.error("【异常增强】the method 【" + methodName + "】 occurs exception: ", e);
    }

    /**
     * 环绕增强：目标方法执行前后分别执行一些代码，发生异常的时候执行另外一些代码
     *
     * @return
     */
/*    @Around(value = "execution(* com.example.demo.controller.*.*(..))")
    public Object aroundMethod(ProceedingJoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Object result = null;
        try {
            log.info("【环绕增强中的--->前置增强】：the method 【" + methodName + "】 begins with " + Arrays.asList(jp.getArgs()));
            //执行目标方法
            result = jp.proceed();
            log.info("【环绕增强中的--->返回增强】：the method 【" + methodName + "】 ends with " + result);
        } catch (Throwable e) {
            result = "error";
            log.info("【环绕增强中的--->异常增强】：the method 【" + methodName + "】 occurs exception " + e);
        }
        log.info("【环绕增强中的--->后置增强】：-----------------end.----------------------");
        return result;
    }*/

}
