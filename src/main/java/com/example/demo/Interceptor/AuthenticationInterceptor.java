package com.example.demo.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.entity.rabc.Users;
import com.example.demo.service.UserService;
import com.example.demo.utility.Authorize;
import com.example.demo.utility.AuthorizeType;
import com.example.demo.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/*
Filter:servlet采用回调的方式实现，可以获取request信息，获取不到方法的参数信息。
Interceptor:采用反射动态代理实现，可以获取request信息，获取不到方法的参数信息。
Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtility jwtUtility;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
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
                DecodedJWT decodedJWT=null;

                // 验证 token
                try {
                    //不用校验自己手动解析
                    decodedJWT = jwtUtility.verifier(token);
                } catch (JWTVerificationException e) {
                    //返回状态码，前端根据装填码判断token 是过期。
                    returnJson(httpServletResponse,"token is expired");


                    return  false;
                }
                //获取Token中自定义信息
                //获取角色信息，此处可做角色权限判断控制。
                String role = decodedJWT.getClaim("role").asString();
                String exp = decodedJWT.getClaim("exp").asString();

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
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            // logger.error("response error",e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }


}
