package com.example.demo.aop.Interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.utility.Authorize;
import com.example.demo.utility.AuthorizeType;
import com.example.demo.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * HandlerInterceptor是springMVC项目中的拦截器，它拦截的目标是请求的地址，比MethodInterceptor先执行。其工作原理是当请求
 * MethodInterceptordr动态代理CGLIB
 *
 *
 *
 * 拦截器实现HandlerInterceptor接口，也可以继承HandlerInterceptorAdapter类
 *
 *
 * 执行顺序：Filter -> Interceptor -> ControllerAdvice -> Aspect -> Controller
 *
 *  通知（Advice）是一种拦截器，用于在请求处理的不同阶段或响应进行全局的处理和定制化操作，
 *  允许开发者在请求处理的特定时刻介入对请求和响应做出修改或扩展。
 *
 * Filter:servlet采用回调的方式实现，可以获取request信息，获取不到请求的方法信息。
 * Interceptor:采用反射动态代理实现，可以获取request信息，可以获取到请求的方法名称，获取不到方法的参数信息。
 * Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数
 * <p>
 *
 *
 * preHandle方法将在Controller处理之前调用的
 * postHandle 会在Controller方法调用之后
 * afterCompletion在当前interceptor的preHandle方法返回true时才执行
 *
 *
 * preHandle-->postHandle-->afterCompletion
 * preHandle return false 就不进入postHandle
 * 可以多个Interceptor
 * <p>
 * <p>
 * <p>
 * 拦截器：拦截不执行，返回false,在response中写入信息，如果有的要执行请用Aspect
 */
//@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
//    HandlerInterceptorAdapter
    @Autowired
    private JWTUtility jwtUtility;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token

//        httpServletRequest.getSession()

        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有Authorize注解，否则跳过认证
        if (method.isAnnotationPresent(Authorize.class)) {
            Authorize authorize = method.getAnnotation(Authorize.class);
            if (authorize.value() == AuthorizeType.UnAuthorize) {
                return true;
            } else {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("UnAuthorize");
                }
                DecodedJWT decodedJWT = null;

                // 验证 token
                try {
                    //crm登录时候会把token做key将登录信息放入redis退出删除key，根据token到redis取值，如果没取到就登录，反之登录成功。
                    // 但是这就是永久有效，没有有效期的限制了。


                    //不用校验自己手动解析
                    decodedJWT = jwtUtility.verifier(token);
                } catch (TokenExpiredException e) {

                    String loginUrl = "http://localhost:8101/user/login?name=fancky&password=pas";

                    // httpServletResponse.addHeader("REDIRECT", "REDIRECT");//

                    //不能用Location参数，自动重定向。浏览器接收的消息头中含有Location信息回自动重定向。
                    //此时浏览器的地址框内的地址还是重定向之前的地址。
                    //        httpServletResponse.addHeader("Location", loginUrl);//重定向地址
                    httpServletResponse.addHeader("RedirectUrl", loginUrl);//重定向地址
                    httpServletResponse.setStatus(302);//http XMLHttpRequest.status!=200就报错：error。302告诉ajax这是重定向

                    returnJson(httpServletResponse, "token is expired");


                    return false;
                } catch (JWTVerificationException e) {
                    //返回状态码，前端根据装填码判断token 是过期。
                    returnJson(httpServletResponse, "token is expired");


                    return false;
                }
                //获取Token中自定义信息
                //获取角色信息，此处可做角色权限判断控制。
                String role = decodedJWT.getClaim("role").asString();
                Date expireDate = decodedJWT.getClaim("expireDate").asDate();


                return true;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {


    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {

//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Cache-Control","no-cache");
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {


            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            // logger.error("response error",e);
        } finally {
            if (writer != null) {
                writer.close();
            }

        }
    }


}
