package com.example.demo.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.entity.rabc.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

/*
另一个开源框架：io.jsonwebtoken
 */
@Component
public class JWTUtility {


    //SECRET key 最好做配置，要经常变动
//    private static final String SECRET = "GQDstcKsx0NHjPOuXOYg5MbeJ1XT0uFiwDVvVBrk";
//    private static final String SECRET1 = "GQDstcKsx0NHjPOuXOYg5MbeJ1XT0uFiwDVvVBr";
    @Value("${demo.JWTSecretKey}")
    private String jWTSecretKey;


    /**
     * 编码：
     * 将用户信息生成token
     * @param user
     * @return
     */
    public String getToken(Users user) {
        //60秒过期
        Date expireDate = new Date(System.currentTimeMillis() + 60 * 1000);
        String token = JWT.create()
                //如果设置了到期时间，验证的时候会判断是否到期
                .withExpiresAt(expireDate)
                //
                //私有的Claims,即自定义字段

                //设置到期日期，此时验证时候不会判断是否过期
                //.withClaim("exp",expireDate.toString())
                .withClaim("userID", user.getId().toString())
                .withClaim("userName", "fancky")
                .withClaim("role", "administrator")
                .sign(Algorithm.HMAC256(jWTSecretKey));
        return token;
    }

    /**
     * 解码：拦截器调用。
     *校验成功返回DecodedJWT对象，否则内部抛出异常。
     * @param token
     * @return
     */
    public DecodedJWT verifier(String token) {
        //SECRET:生成Token的key和解码、验证的key必须一样，否则报下面错误。
        //The Token's Signature resulted invalid when verified using the Algorithm: HmacSHA256
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jWTSecretKey)).build();
        return jwtVerifier.verify(token);
    }


}
