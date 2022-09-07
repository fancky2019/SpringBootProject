package com.example.demo.controller;

import com.example.demo.model.entity.shiro.User;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.service.shiro.UserService;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/shiro")
public class ShiroController {

    private static final Logger LOGGER = LogManager.getLogger(ShiroController.class);

    @Autowired
    UserService userService;

    //    没有默认值，如果不配置method，
//
//    则以任何请求形式
//
//    RequestMethod.GET，
//    RequestMethod.POST，
//    RequestMethod.PUT，
//    RequestMethod.DELETE都可以访问得到。
    //登录
    @RequestMapping("/login")
    public MessageResult<Void> login(String userName, String password) {
        MessageResult<Void> messageResult = new MessageResult<>();

//        Void.class
        //     MDC.put("traceId", traceId);//traceId在过滤器的destroy()中清除
        //   messageResult.setTraceId(MDC.get("traceId"));
//        return ResponseEntity.ok(messageResult);
//        logger.error(ex.toString());// 不会打出异常的堆栈信息

        try {
            // 获取Subject实例对象，用户实例
            Subject currentUser = SecurityUtils.getSubject();

            // 将用户名和密码封装到UsernamePasswordToken
            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
            // 传到 MyShiroRealm 类中的方法进行认证
            currentUser.login(token);
//            CacheUser cacheUser;
            String salt = RandomStringUtils.randomAlphanumeric(10);
            ;
            // 构建缓存用户信息返回给前端
            String userName1 = (String) currentUser.getPrincipals().getPrimaryPrincipal();
//            cacheUser = CacheUser.builder()
//                    .token(currentUser.getSession().getId().toString())
//                    .build();

            User sessionUser = (User) SecurityUtils.getSubject().getSession().getAttribute("UserInfo");

        } catch (Exception e) {
            messageResult.setCode(500);

            messageResult.setSuccess(false);
            LOGGER.error("", e);//用此重载，打印异常的所有信息
            messageResult.setMessage(e.getMessage());
        }
        return messageResult;
    }

    //退出登录
    @RequestMapping("/logout")
    public MessageResult<Void> logout() {
        MessageResult<Void> messageResult = new MessageResult<>();

        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return messageResult;
    }

    @RequestMapping("/register")
    public MessageResult<Void> register(@RequestBody User user) {
        MessageResult<Void> messageResult = new MessageResult<>();

        //生成随机的字符串，即我们要用到的盐值
        String salt = RandomStringUtils.randomAlphanumeric(10);

        //使用MD5加密
        Md5Hash password = new Md5Hash(user.getPassword(), salt, 1024);
        String saltPassword = String.valueOf(password);

        //数据库保存加盐后的密码
        user.setPassword(saltPassword);
        user.setUserId("13956914410");
        user.setSalt(salt);
        //入库 user
        user.setAge(27);
        user.setCreateTime(LocalDateTime.now());
        user.setEmail("709737@qq.com");
        user.setPhone("13956914410");
        user.setSex(1);
        user.setStatus(1);
        userService.insert(user);
        return messageResult;
    }

    @GetMapping("list")
    public MessageResult<Void> getList() {
        MessageResult<Void> messageResult = new MessageResult<>();


        return messageResult;
    }


}
