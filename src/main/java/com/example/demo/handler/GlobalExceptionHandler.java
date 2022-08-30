package com.example.demo.handler;

import com.example.demo.controller.UserController;
import com.example.demo.model.viewModel.MessageResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * 该类放在单独一个文件夹
 * 只能捕捉进入controller里异常的代码。
 *
 * extends ResponseEntityExceptionHandler
 *
 *
 * @ControllerAdvice :注解定义全局异常处理类
 * @ExceptionHandler :注解声明异常处理方法
 *
 */
@ControllerAdvice
//@RestControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);


    /**
     * 404无法进入此方法。404被tomcat拦截了
     * 自定义返回数据格式
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public MessageResult<Void> exceptionHandler(Exception ex, WebRequest request) {
        MessageResult<Void> messageResult = new MessageResult<>();
        messageResult.setCode(500);
        messageResult.setMessage(ex.getMessage());
        messageResult.setSuccess(false);
//        Void.class
        //     MDC.put("traceId", traceId);//traceId在过滤器的destroy()中清除
     //   messageResult.setTraceId(MDC.get("traceId"));
//        return ResponseEntity.ok(messageResult);
//        logger.error(ex.toString());// 不会打出异常的堆栈信息
        logger.error("",ex);//用此重载，打印异常的所有信息
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

}
