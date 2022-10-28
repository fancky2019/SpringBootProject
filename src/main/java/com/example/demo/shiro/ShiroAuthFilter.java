package com.example.demo.shiro;

import com.example.demo.model.viewModel.MessageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return request instanceof ShiroHttpServletRequest && StringUtils.equalsIgnoreCase("OPTIONS", ((ShiroHttpServletRequest) request).getMethod()) ? true : super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        log.info(((HttpServletRequest) request).getRequestURI());
        //   CppicResponseBody<String> cppicResponseBody = CppicResponseBody.error(ShiroResultEnum.NEED_LOGIN);


        //返回充电箱url
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
