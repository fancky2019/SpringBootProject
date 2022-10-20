package com.example.demo.aop.advice;

import com.example.demo.utility.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;

//@RestControllerAdvice
@Slf4j
public class EncryptDecryptAdvice implements ResponseBodyAdvice<Object>, RequestBodyAdvice {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

    private HashSet<String> uriHashSet;
    @Value("${gx.encrypt.allowlist}")
    private String allowList;

    @PostConstruct
    public void init() {
        if (uriHashSet == null) {
            uriHashSet = new HashSet<String>();
        }
        if (uriHashSet.size() == 0) {
            if (StringUtils.isNotEmpty(allowList)) {
                String[] paths = allowList.split(";");
                for (String path : paths) {
                    uriHashSet.add(path);
                }
            }
        }
    }

//    private HashSet<String> getUriHashSet() {
//        if (uriHashSet == null) {
//            uriHashSet = new HashSet<String>();
//
//        }
//        if (uriHashSet.size() == 0) {
//            if (StringUtils.isNotEmpty(allowList)) {
//                String[] paths = allowList.split(";");
//                for (String path : paths) {
//                    uriHashSet.add(path);
//                }
//            }
//        }
//        return uriHashSet;
//    }

    //RequestBodyAdvice
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {

        HttpSession session = request.getSession();
//        URL :http://37.220.51.158:8281/login;URI:/login
        String uri = request.getRequestURI();
        if (uriHashSet.contains(uri)) {
            return httpInputMessage;
        }


        InputStream inputStream = httpInputMessage.getBody();
        StringBuilder builder = new StringBuilder();
        if (!ObjectUtils.isEmpty(inputStream)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                builder.append(charBuffer, 0, bytesRead);
            }
        }
        String requestBodyStr = builder.toString();
        log.info("start decode request body:{}", requestBodyStr);
        if (StringUtils.isEmpty(requestBodyStr)) {
            return httpInputMessage;
        }
        String decodeStr = "";
        try {

            //测试数据
//            String requestBodyStr1 = "123456123456123456123456123456123456123456" +
//                    "123456123456123456123456123456123456123456" +
//                    "123456123456123456123456123456123456123456" +
//                    "123456123456123456123456123456123456123456" +
//                    "123456123456123456123456123456123456123456" +
//                    "123456123456123456123456123456123456123456" +
//                    "dsds123456123456123456123456123456123456123456ddsds" +
//                    "dsds12345612345612345612345612345612345612345645454dsds";
////        String requestBodyStr1 = "ssfssffs";
//            String ss = Base64.encodeBase64String(RSAUtil.encrypt(requestBodyStr1.getBytes(), RSAUtil.getPublicKey()));
//            decodeStr = new String(RSAUtil.decrypt(Base64.decodeBase64(ss), RSAUtil.getPrivateKey()));
//
//            String signStr = RSAUtil.sign(requestBodyStr1.getBytes(), RSAUtil.getPrivateKey());
//            boolean re = RSAUtil.verify(requestBodyStr1.getBytes(),signStr, RSAUtil.getPublicKey());

//            String encodeStr = Base64.encodeBase64String(RSAUtil.encrypt(requestBodyStr.getBytes()));


            decodeStr = new String(RSAUtil.decrypt(Base64.decodeBase64(requestBodyStr)));
            log.info("end decode request body:{}", decodeStr);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

//        return HttpInputMessageImp.builder().headers(httpInputMessage.getHeaders()).body(new ByteArrayInputStream(decodeStr.getBytes("UTF-8"))).build();

        return HttpInputMessageImp.builder().headers(httpInputMessage.getHeaders()).body(new ByteArrayInputStream(decodeStr.getBytes())).build();
    }

    //解密
    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return null;
    }


    //ResponseBodyAdvice
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    //加密
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        try {
            String jsonStr = objectMapper.writeValueAsString(o);
            log.info("start encode response:{}", jsonStr);
            String encodeStr = Base64Utils.encodeToUrlSafeString(RSAUtil.decrypt(jsonStr.getBytes()));


            String encodeStr1 = Base64Utils.encodeToString(RSAUtil.decrypt(jsonStr.getBytes()));

            log.info("end encode response:{}", encodeStr);
            return encodeStr;
        } catch (Exception e) {
            return o;
        }


    }


}
