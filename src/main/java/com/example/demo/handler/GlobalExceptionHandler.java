package com.example.demo.handler;

import com.example.demo.controller.UserController;
import com.example.demo.model.viewModel.MessageResult;
import feign.FeignException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;

/**
 *
 * @ControllerAdvice 可以被应用到所有标注为 @Controller 的类
 *组合使用：@ControllerAdvice 可以与其他注解如 @ExceptionHandler、@ModelAttribute、@InitBinder 等一起使用，处理异常、共享模型数据、进行数据绑定等。
 *
 *
 *Spring 默认会按照方法声明顺序执行同一类中的多个 @ModelAttribute 方法，但最可靠的方式是通过方法参数显式声明依赖关系，而不是依赖隐式的执行顺序。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *默认只能捕捉到主线程中的异常，无法捕捉到线程池内线程抛出的异常。这是因为线程池内的线程是由线程池管理的，异常不会自动传播到主线程。
 *
 * 线程池（异步任务）中的异常 不会被 Spring 的全局异常处理器捕获，因为这些异常发生在线程池的子线程中，而 Spring 的异常处理器只作用于主线程（MVC 请求线程）。
 *
 *
 * 在Spring中，Advice都是通过Interceptor来实现的
 *
 *
 * 该类放在单独一个文件夹
 * 只能捕捉进入controller里异常的代码。
 *
 * extends ResponseEntityExceptionHandler
 *
 * @ControllerAdvice :注解定义全局异常处理类
 * @ExceptionHandler :注解声明异常处理方法
 */
@Slf4j
@ControllerAdvice
//@Order(Ordered.LOWEST_PRECEDENCE)
//@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);
    private final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();


    @Autowired
    private HttpServletResponse httpServletResponse;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletRequest request;
    /**
     * 处理404 Not Found异常
     * 需配合ErrorController使用
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public MessageResult<String> handle404(NoHandlerFoundException ex) {
        log.error("404异常 - 请求URL: {}", request.getRequestURL());
        String  errorMsg=  "资源不存在: " + request.getRequestURI();
        MessageResult<Void> result = new MessageResult<>();
        result.setCode(HttpStatus.NOT_FOUND.value());
        result.setMessage(errorMsg);
        return MessageResult.faile();

    }

    /**
     * 404无法进入此方法。404被tomcat拦截了
     * 自定义返回数据格式
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public MessageResult<Void> exceptionHandler(Exception ex, WebRequest request) throws Exception {
//ResponseEntity.ok()
//        if (ex instanceof FeignException) {
//            throw ex; // 重新抛出，让Feign的Fallback处理
//        }
        MessageResult<Void> messageResult = new MessageResult<>();
        messageResult.setCode(500);
        String msg = "";

//        if (ex instanceof UndeclaredThrowableException) {
//            UndeclaredThrowableException undeclaredThrowableException = (UndeclaredThrowableException) ex;
//            msg = undeclaredThrowableException.getUndeclaredThrowable().getMessage();
//        } else {
//            msg = ex.getMessage();
//        }
        msg = ex.getMessage();
        messageResult.setMessage(ex.getMessage());
        messageResult.setSuccess(false);
//        Void.class
        //     MDC.put("traceId", traceId);//traceId在过滤器的destroy()中清除
        //   messageResult.setTraceId(MDC.get("traceId"));
//        return ResponseEntity.ok(messageResult);
//        logger.error(ex.toString());// 不会打出异常的堆栈信息
        logger.error("", ex);//用此重载，打印异常的所有信息
        return messageResult;
    }

//    /**
//     * 数据校验全局处理
//     * MethodArgumentNotValidException是@RequestBody和@Validated配合时产生的异常，比如在传参时如果前端的json数据里部分缺失@RequestBody修饰的实体类的属性就会产生这个异常。
//     */
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    @ResponseBody
//    public  MessageResult<Void> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e)
//    {
//
//        MessageResult<Void> messageResult = new MessageResult<>();
//        messageResult.setCode(500);
//        messageResult.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
//
////        logger.error(ex.toString());// 不会打出异常的堆栈信息
////        logger.error("",ex);//用此重载，打印异常的所有信息
//        return messageResult;
//    }
//
////    /**
////     * 数据校验全局处理
////     * BindException是@Validated使用校验失败时产生的异常
////     */
////    @ExceptionHandler(value = BindException.class)
////    @ResponseBody
////    public  MessageResult<Void> BindExceptionHandler(BindException e)
////    {
////
////        //捕获数据校验异常
////        MessageResult resultInfo = new MessageResult();
////        resultInfo.setCode(500);
////        //获取实体类定义的校验注解字段上的message作为异常信息，@NotBlank(message = "用户密码不能为空！")异常信息即为"用户密码不能为空！"
////        resultInfo.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
////        return resultInfo;
////    }
//
//    @ExceptionHandler({UnauthorizedException.class})
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public MessageResult<Void> processUnauthenticatedException(NativeWebRequest request, UnauthorizedException e) {
//        MessageResult<Void> messageResult = new MessageResult<>();
//        messageResult.setCode(500);
//        messageResult.setMessage(e.getMessage());
//        messageResult.setSuccess(false);
////        Void.class
//        //     MDC.put("traceId", traceId);//traceId在过滤器的destroy()中清除
//        //   messageResult.setTraceId(MDC.get("traceId"));
////        return ResponseEntity.ok(messageResult);
////        logger.error(ex.toString());// 不会打出异常的堆栈信息
//        logger.error("",e);//用此重载，打印异常的所有信息
//        return messageResult;
//    }

//region 耗时日志

//一个类中多个方法都有  @ModelAttribute 注解，默认按出现顺序执行
//ModelAttribute 会在 controller 之前执行，不能获得controller 耗时时间，可用aspect 或  Interceptor 或者 filter



    @ExceptionHandler({
            FeignException.class,
            FeignException.Unauthorized.class,
            FeignException.Forbidden.class,
            FeignException.NotFound.class  // 新增404处理
    })
    public ResponseEntity<MessageResult<Void>> handleFeignException(FeignException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMsg = "服务调用异常:"+ex.getMessage();

        // 根据具体异常类型细化处理
        if (ex instanceof FeignException.Unauthorized) {
            status = HttpStatus.UNAUTHORIZED;
            errorMsg = "认证失败，请检查访问凭证";
        }
        else if (ex instanceof FeignException.Forbidden) {
            status = HttpStatus.FORBIDDEN;
            errorMsg = "权限不足，禁止访问";
        }
        else if (ex instanceof FeignException.NotFound) {
            status = HttpStatus.NOT_FOUND;
            errorMsg = "请求资源不存在: " + ex.request().url();
        }

        // 统一响应构造
        MessageResult<Void> result = new MessageResult<>();
        result.setCode(status.value());
        result.setMessage(errorMsg);
//        return MessageResult.faile();
        return ResponseEntity
                .status(status)
                .body(result);


//        ResponseEntity 是Spring提供的完整响应封装器，可以精确控制：
//        return ResponseEntity
//                .status(404)                // HTTP状态码
//                .header("X-Custom", "123")  // 自定义头
//                .body(result);              // 响应体

//        直接返回MessageResult时，Spring会自动包装为200 OK响应，状态码不可控
    }



    @ModelAttribute
    public void recordStartTime() {
        startTimeHolder.set(System.currentTimeMillis());
        log.debug("recordStartTime1");
    }

    @ModelAttribute
    public void logAndRecordMetrics(HttpServletRequest request) {
        log.debug("logAndRecordMetrics2");
        Long startTime = startTimeHolder.get();
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String uri = request.getRequestURI();
            String method = request.getMethod();

            // 记录日志
            log.debug("请求指标 || method={} || uri={} || duration={}ms", method, uri, duration);

//            // 记录到Micrometer指标
//            meterRegistry.timer("http.server.requests")
//                    .tags("uri", uri, "method", method)
//                    .record(duration, TimeUnit.MILLISECONDS);

            startTimeHolder.remove();
        }
    }
    //endregion

}
