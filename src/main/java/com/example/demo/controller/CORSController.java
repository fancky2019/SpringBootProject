package com.example.demo.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
可在在过滤器中加入服务端跨域许可
 */
@RestController
@RequestMapping("/cors")
public class CORSController {


    @GetMapping("/getAuthorization")
    public String getAuthorization() {
        //   HttpServletRequest httpServletRequest= ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();


        //跨域设置。不然头部信息传不回去。 在配置或Filter里面跨域设置，建议在配置里设置。
//        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");


        String loginUrl = "http://localhost:8101/user/login?name=fancky&password=pas";
        httpServletResponse.addHeader("Content-Type", "application/json");
        httpServletResponse.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向

        //不能用Location参数，自动重定向。浏览器接收的消息头中含有Location信息回自动重定向。
        //此时浏览器的地址框内的地址还是重定向之前的地址。
//        httpServletResponse.addHeader("Location", loginUrl);//重定向地址
        httpServletResponse.addHeader("RedirectUrl", loginUrl);//重定向地址
        httpServletResponse.setStatus(302);//http XMLHttpRequest.status!=200就报错：error
       /* 通过CORS方式解决的ajax跨域,是获取不到请求头的。getResponseHeader的值一直为空。
        要通过Access-Control-Expose-Headers来设置响应头的白名单。
        */
        //将想要传递的字段设置一下。才能获取到值。
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "REDIRECT,RedirectUrl");


        return "getAuthorization fail";
    }

  /*
    返回的不是对象，ajax的dataType属性不用设置，否则前端报parse error 错
   */
    @GetMapping("/getData")
    public String getData() {
        return "getData";
    }


}
