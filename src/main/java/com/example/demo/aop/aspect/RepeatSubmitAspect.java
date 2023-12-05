package com.example.demo.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 注解代码浸入太大，
 * 1、唯一索引，
 * 2、（1）前台打开新增页面访问后台获取该表的token (存储在redis 中的uuid)key:用户id_功能.value token
 *        获取token时候判断用户有没有没有过期时间的token，有就说明已请求，直接返回
 *   （2） 检测前段提交的token是不是在redis 中而且过期时间不为0，验证通过入库成功更新redis 中的token过期时间
 * 3、对于篡改的api请求通过加密方式，防止信息泄密。https://host:port//api。 nginx
 *
 */
@Aspect
@Component
@Order(100)//多个切面用order制定顺序，数值越小，越先执行
public class RepeatSubmitAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;


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
