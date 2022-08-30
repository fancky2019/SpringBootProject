package com.example.demo.config;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@Configuration
//public class OkHttpClientConfig {
//
//    @Bean
//    public OkHttpClient okHttpClient() {
//        return new OkHttpClient.Builder()
//                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间,默认10s
//                .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
//                .cookieJar(new CookieJar() {
//                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
//
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                       // cookieStore.put(url.host(), cookies);
//                    }
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
////                        List<Cookie> cookies = cookieStore.get(url.host());
////                        return cookies != null ? cookies : new ArrayList<Cookie>();
//
//                        return  null;
//                    }
//                })
//                .build();
//    }
//}
