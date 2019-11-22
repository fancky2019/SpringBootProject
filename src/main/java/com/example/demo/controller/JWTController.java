package com.example.demo.controller;

import com.example.demo.model.entity.rabc.Users;
import com.example.demo.utility.Authorize;
import com.example.demo.utility.AuthorizeType;
import com.example.demo.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
public class JWTController {

    @Autowired
    private JWTUtility jWTUtility;

    @GetMapping("/getToken")
    public String getToken() {
        Users users = new Users();
        users.setId(1);
        users.setPassword("123456");
        return jWTUtility.getToken(users);
    }

    //PostMan:http 请求头(Headers)添加
    //Key           Value
    //token:        token字符串
    @Authorize(AuthorizeType.Authorize)
    @GetMapping("/authorise")
    public String authorise() {

        try {
            return "authorise";
        } catch (Exception e) {
           return e.getMessage();
        }
    }

    @Authorize(AuthorizeType.UnAuthorize)
    @GetMapping("/unAuthorise")
    public String unAuthorise() {
        return "unAuthorise";
    }
}
