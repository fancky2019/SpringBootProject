package com.example.demo.dynamicDataSource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/*
拦截启用顺序：依次是过滤器、拦截器、切片，这是服务正常的时候，服务异常时，最先捕获的是切片、ControllerAdvice注解类、拦截器、过滤器
正常的执行顺序是：@Around ->@Before->主方法体->@Around中pjp.proceed()->@After->@AfterReturning

Filter:servlet采用回调的方式实现，可以获取request信息，获取不到方法的参数信息。
Interceptor:采用反射动态代理实现，可以获取request信息，还可以获取请求的类名、方法名，但拦截器无法获取请求参数的值
Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数
 */
/*
@Component 的目录必须在主目录下，不然扫描不到，必须加下面注解
@ComponentScan(basePackages = {"com.example.demo.dynamicDataSource.DynamicDataSourceAspect})
 */

/**
 * service 层开启事务则不能动态切换
 *  通过加注解 @DataSourceAnnotation(DataSourceStrings.READER)
 *  反射动态切换数据源（读、写Server）
 * <p>
 * 1、启动类设置
 * #//由于采用多数据源，禁用springboot默认的数据源配置，多数据源不适合微服务设计理念废弃。采用分布式事务。
 * 多数据源：用作读写分离，主写，从读，会涉及主从不一致，强制主读情况
 * sharding-jdbc 强制主读（之后查询语句）
 * HintManager.getInstance().setWriteRouteOnly(); //参见项目shardingreadwrite
 *
 * #@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
 *
 * 2、DataSourceConfig 配置类启用。
 * 3、默认数据库url配置注释，启用write、reader。
 * 4、测试类ValvulasService
 */
//@Aspect   //@ControllerAdvice 一般用于Controller
//@Component
public class DynamicDataSourceAspect {


//    @Before("@annotation(DataSourceAnnotation)")
//    public void before(JoinPoint joinPoint) {
//
//    }

//    @Before("@annotation(dataSource)")
//    public void before(JoinPoint joinPoint, DataSourceAnnotation dataSource) {
//
//    }




    // service  添加注解 @DataSourceAnnotation(DataSourceStrings.WRITER)
//    测试代码： com.example.demo.service.valvulas.ValvulasService;


    //设置注解类型并获取注解的值
    //设置注解类型并获取注解的值
//@Before(value = "@annotation(dataSource)", argNames = "dataSource")
    @Before("@annotation(dataSource)")
    public void beforeSwitchDataSource(DataSourceAnnotation dataSource) {
        DataSourceContextHolder.setDB(dataSource.value());
    }

//    @Pointcut(value = "execution(public * com.leo.controller.HelloController.hello*(..))")
//    public void pointCut() {
//
//    }
    /*
    正常的执行顺序是：@Around ->@Before->主方法体->@Around中pjp.proceed()->@After->@AfterReturning
     */
    /**
     * 在进入类之前执行，然后返回pjp.proceed()之前执行before，再执行方法体，在到after
     *
     * @param
     */
//    @Around(value = "pointCut()")
//    public Object aroundLog(ProceedingJoinPoint pjp) throws Throwable {
//        System.out.println("进入LogAop的aroundLogger");
//        return pjp.proceed();
//    }
//    @Around("execution(* com.company.controller.*.*(..))")
//    public Object run1(ProceedingJoinPoint joinPoint) throws Throwable {
//        //获取方法参数值数组
//        Object[] args = joinPoint.getArgs();
//        //得到其方法签名
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        //获取方法参数类型数组
//        Class[] paramTypeArray = methodSignature.getParameterTypes();
//        if (EntityManager.class.isAssignableFrom(paramTypeArray[paramTypeArray.length - 1])) {
//            //如果方法的参数列表最后一个参数是entityManager类型，则给其赋值
//            args[args.length - 1] = entityManager;
//        }
//        logger.info("请求参数为{}",args);
//        //动态修改其参数
//        //注意，如果调用joinPoint.proceed()方法，则修改的参数值不会生效，必须调用joinPoint.proceed(Object[] args)
//        Object result = joinPoint.proceed(args);
//        logger.info("响应结果为{}",result);
//        //如果这里不返回result，则目标对象实际返回值会被置为null
//        return result;
//    }



    @After("@annotation(DataSourceAnnotation)")
    public void afterSwitchDataSource() {
        DataSourceContextHolder.clearDB();
    }

//    //    HttpServlet
//    // HttpServletRequest
////    HttpServletResponse
////    DispatcherServlet
//    @Around("@annotation(DataSourceAnnotation)")
//    public Object switchDB(ProceedingJoinPoint point) throws Throwable {
//        // ...(方法执行前的逻辑)
//        setDB(point);
//        Object result = point.proceed();
//        // ...(方法执行后的逻辑)
//        return result;
//    }

    public void setDB(JoinPoint point) {
        //获得当前访问的class
        Class<?> className = point.getTarget().getClass();
        //获得访问的方法名
        String methodName = point.getSignature().getName();
        //得到方法的参数的类型
        Class[] argClass = ((MethodSignature) point.getSignature()).getParameterTypes();
        String dataSource = "";
        try {
            // 得到访问的方法对象
            Method method = className.getMethod(methodName, argClass);
            // 判断是否存在@DS注解
            if (method.isAnnotationPresent(DataSourceAnnotation.class)) {
                DataSourceAnnotation annotation = method.getAnnotation(DataSourceAnnotation.class);
                // 取出注解中的数据源名
                dataSource = annotation.value();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 切换数据源
        DataSourceContextHolder.setDB(dataSource);
    }
}
