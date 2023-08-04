package com.example.demo.shiro;

import com.example.demo.model.viewModel.MessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

public class ShiroAuthFilter extends FormAuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(ShiroAuthFilter.class);

    private ObjectMapper objectMapper;

    public ShiroAuthFilter(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }


    /**
     * 因为发现设置的successUrl没生效，所以追踪源码发现如果SavedRequest对象不为null,则它会覆盖掉我们设置
     * 的successUrl，所以我们要重写onLoginSuccess方法，在它覆盖掉我们设置的successUrl之前，去除掉
     * SavedRequest对象,SavedRequest对象的获取方式为：
     * savedRequest = (SavedRequest) session.getAttribute(SAVED_REQUEST_KEY);
     * public static final String SAVED_REQUEST_KEY = "shiroSavedRequest";
     * 解决方案：从session对象中移出shiroSavedRequest
     */
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
//                                     ServletResponse response) throws Exception {
//        if (!StringUtils.isEmpty(getSuccessUrl())) {
//            // getSession(false)：如果当前session为null,则返回null,而不是创建一个新的session
//            Session session = subject.getSession(false);
//            if (session != null) {
//                session.removeAttribute("shiroSavedRequest");
//            }
//        }
//        return super.onLoginSuccess(token, subject, request, response);
//    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return request instanceof ShiroHttpServletRequest && StringUtils.equalsIgnoreCase("OPTIONS", ((ShiroHttpServletRequest) request).getMethod()) ? true : super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.info(((HttpServletRequest) request).getRequestURI());
        //   CppicResponseBody<String> cppicResponseBody = CppicResponseBody.error(ShiroResultEnum.NEED_LOGIN);

//        issueSuccessRedirect(request, response);


        //前段收到重定向消息之后，保存当前页面，登录成功之后再跳转到之前页面

        //退出时候要删除之前保存的信息 session

        //超时重定向：登录的时候保存登录时间，下次访问时间和登录时间比较
        //返回重定向url：系统登录地址
        String redirectUrl = "";

        MessageResult<String> responseBody = new MessageResult<>();
        responseBody.setData(redirectUrl);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        Throwable var6 = null;


        try {
            printWriter.write(objectMapper.writeValueAsString(responseBody));
            printWriter.flush();
        } catch (Throwable var15) {
            var6 = var15;
            throw var15;
        } finally {
            if (printWriter != null) {
                if (var6 != null) {
                    try {
                        printWriter.close();
                    } catch (Throwable var14) {
                        var6.addSuppressed(var14);
                    }
                } else {
                    printWriter.close();
                }
            }

        }

        return false;
    }
}
