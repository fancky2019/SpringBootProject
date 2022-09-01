//package com.example.demo.controller;
//
//import com.example.demo.model.entity.shiro.User;
//import com.example.demo.model.viewModel.MessageResult;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/shiro")
//public class ShiroController {
//
//    private static final Logger LOGGER = LogManager.getLogger(ShiroController.class);
//
//
//    //    没有默认值，如果不配置method，
////
////    则以任何请求形式
////
////    RequestMethod.GET，
////    RequestMethod.POST，
////    RequestMethod.PUT，
////    RequestMethod.DELETE都可以访问得到。
//    //登录
//    @RequestMapping("/login")
//    public MessageResult<Void> login(String userName, String password) {
//        MessageResult<Void> messageResult = new MessageResult<>();
//
////        Void.class
//        //     MDC.put("traceId", traceId);//traceId在过滤器的destroy()中清除
//        //   messageResult.setTraceId(MDC.get("traceId"));
////        return ResponseEntity.ok(messageResult);
////        logger.error(ex.toString());// 不会打出异常的堆栈信息
//
//        try {
//            // 获取Subject实例对象，用户实例
//            Subject currentUser = SecurityUtils.getSubject();
//
//            // 将用户名和密码封装到UsernamePasswordToken
//            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
//            // 传到 MyShiroRealm 类中的方法进行认证
//            currentUser.login(token);
////            CacheUser cacheUser;
//
//            // 构建缓存用户信息返回给前端
//            User user = (User) currentUser.getPrincipals().getPrimaryPrincipal();
////            cacheUser = CacheUser.builder()
////                    .token(currentUser.getSession().getId().toString())
////                    .build();
//
//
//        }  catch (Exception e) {
//            messageResult.setCode(500);
//
//            messageResult.setSuccess(false);
//            LOGGER.error("",e);//用此重载，打印异常的所有信息
//            messageResult.setMessage(e.getMessage());
//        }
//        return messageResult;
//    }
//
//    //退出登录
//    @RequestMapping("/logout")
//    public MessageResult<Void> logout(){
//        MessageResult<Void> messageResult = new MessageResult<>();
//
//        Subject subject = SecurityUtils.getSubject();
//        subject.logout();
//        return messageResult;
//    }
//
//
//}
