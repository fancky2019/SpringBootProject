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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

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
                // 验证 token
                DecodedJWT decodedJWT = jwtUtility.verifier(token);
                //获取Token中自定义信息
                String role = decodedJWT.getClaim("role").asString();

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
}
