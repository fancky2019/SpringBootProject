package com.example.demo.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * 前段请求：
 * 一锁二判三更新四数据库兜底 唯一约束
 *
 * rpc ：request消息中加requestId
 *
 * 1、唯一索引，
 * 2、（1）前台打开新增页面访问后台获取该表的token (存储在redis 中的uuid)key:用户id_功能.value token
 *        获取token时候判断用户有没有没有过期时间的token，有就说明已请求，直接返回
 *   （2） 检测前段提交的token是不是在redis 中而且过期时间不为0，验证通过更新redis 中的token过期时间
 * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
 *
 *
 * 新增、修改可以在跳转的时候从后端获取token
 *
 *
 *
 * Spring AOP 的执行顺序由切面的优先级决定，优先级可以通过 @Order 注解或实现 Ordered 接口来指定。数值越小，优先级越高。
 *不指定Order,那么Order是默认值->Integer.MAX_VALUE. 如果Order相同,则是按照切面字母的顺序来执行切面.比如@Transactional和@Cacheable->
 * 对应的切面是TransactionInterceptor和CacheInterceptor,则先执行@Cacheable的切面
 * 默认情况下：
 * @Transactional 的优先级较低（通常为 Ordered.LOWEST_PRECEDENCE，即 Integer.MAX_VALUE）。
 * 自定义切面的优先级较高（如果没有显式指定 @Order，默认为 Ordered.LOWEST_PRECEDENCE）。
 *
 *
 *如果不在配置类中指定bean注册顺序，就在Aspect加 @Order(202) 注解指定顺序
 * 自定义的切面（默认的是Integer.MAX优先级最低）和事务切面（默认的是Integer.MAX优先级最低）优先级是一样的，
 * 但是自定义的排在后面会先执行，因为spring扫描的时候会先扫描事务相关的。
 *
 * @Order 注解通常用于 AOP 切面、拦截器、过滤器 等组件，以控制它们的执行顺序。
 *通过实现 Ordered 接口或使用 @Order 注解，可以明确指定组件的优先级，从而控制它们的执行顺序
 *LockAnnotationAdvisor 实现了Ordered接口
 *     Lock4j 内部配置类LockAutoConfiguration注册bean LockAnnotationAdvisor 时候设置order =Integer.MIN_VALUE
 *      return new LockAnnotationAdvisor(lockInterceptor, Integer.MIN_VALUE);
 *
 *      从而保证@ Lock4j 优先@Transactional 切面先执行
 *
 *
 *
 *
 *在普通 Bean 上使用 @Order 注解不会生效，因为 @Order 主要用于控制切面、拦截器、过滤器等组件的执行顺序。
 *
 * 如果需要控制普通 Bean 的初始化顺序，可以使用 @DependsOn、SmartLifecycle 或 @PostConstruct。
 *
 * @Order 的正确使用场景包括 AOP 切面、拦截器、过滤器等需要明确执行顺序的组件。
 *
 *  @Order / Ordered接口 不控制实例化顺序，只控制执行顺序
 *  Ordered / @Order 只跟特定一些注解生效 如：@Compent @Service … 不生效的如： @WebFilter
 *
 */


@Aspect
@Component
@Order(100)//多个切面用order制定顺序，数值越小，越先执行
public class RepeatSubmitAspect {

    //bean执行顺序
//    com.example.demo.model.pojo.SpringLifeCycleBean

//    Ordered.LOWEST_PRECEDENCE
    @Autowired
    private HttpServletRequest httpServletRequest;

     // 切入带有@NoRepeatSubmit注解的方法。有@NoRepeatSubmit注解的方法才会拦截
    //    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
//    @Transactional
    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around("pointCut(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) throws Throwable {

        String path=httpServletRequest.getRequestURI();
       // httpServletRequest.getServletPath();
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//        //获取注解中的防重间隔时间
//        MethodSignature signature = (MethodSignature) point.getSignature();
//        Method method = signature.getMethod();
//        NoRepeatSubmit annotation = method.getAnnotation(NoRepeatSubmit.class);
//        long timeOut = annotation.timeOut();
//        String checkTypeName = annotation.requestType().name();
//
//        int lockSeconds = noRepeatSubmit.lockTime();
//
//        HttpServletRequest request = RequestUtils.getRequest();
//        Assert.notNull(request, "request can not null");
//
//        // 此处可以用token(请求的用户)
//        String token = request.getHeader("Authorization");
//        //请求的地址
//        String path = request.getServletPath();
//        //通过用户和请求地址标记唯一的key用来防止重复提交
//        String key = getKey(token, path);
//        //通过redis工具类方法判断redis里是否存在
//        //若存在则说明短时间（过期时间）内该用户访问过该地址，否则则没有（这里存的key加上了我这个项目的访问路径，其实不加也没事）
//        boolean isSuccess = redisUtil.exists(RedisKeyConstant.BASE_DIR + key);
//
//        if (isSuccess)
//            //如果访问过该地址，抛出异常
//            //三个参数分别为 http：状态码、自己代码封装的code码、报错信息
//            throw new BusinessException(CustomHttpStatus.BAD_REQUEST.value(), BusiCodeEnum
//                    .PARAM_CUSTOM_ERROR.getCode(), "操作频繁，请稍后再试！");
//        //if语句只有一句话的话不拥有括号，到这里if判断就结束了
//        //没有访问过则在redis存入key，上面如果加了，这里也要加上，确定key是相同的
//        //过期时间 之前设置的*秒，这里的value是随意取的，因为与他也没关系
//        redisUtil.set(RedisKeyConstant.BASE_DIR + key, 1,
//                CommonConstant.SECONDS * lockSeconds, TimeUnit.SECONDS);
        return pjp.proceed();
    }

    private String getKey(String token, String path) {
        return token + path;
    }

}
