package com.example.demo.controller;

import com.example.demo.model.dto.EosStudentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * https://square.github.io/okhttp/recipes/
 * <p>
 * 测试项目在：AL.WorkSpace.ordermigratedbtool
 */
@RestController
@RequestMapping("/okHttp")
public class OkHttpController {


    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper mapper;

    public OkHttpController() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间,默认10s
                .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
                .build();
    }

    //http://localhost:8101/fileupload/uploadStatus
    @GetMapping("/get")
    public String get() throws Exception {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:8101/fileupload/getUser")
                .newBuilder();
        urlBuilder.addQueryParameter("studentName", "fancky");

        //同步执行，异步参见官网demo
        Request request = new Request.Builder()
                .url(urlBuilder.build())
//                .header("User-Agent", "OkHttp Headers.java")
//                .addHeader("Accept", "application/json; q=0.5")
//                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        //同步调用
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
////            Headers responseHeaders = response.headers();
////            for (int i = 0; i < responseHeaders.size(); i++) {
////                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
////            }
//            ResponseBody responseBody = Objects.requireNonNull(response.body());
//            String result = Objects.requireNonNull(response.body()).string();
//
//            return result;
//        }


        //异步调用
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.e("测试", e+"");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();

            }

        });
        return "";
    }

    @PostMapping("/post")
    public String post() throws Exception {
        //public final class FormBody extends RequestBody
//        FormBody.Builder formBodyBuilder = new FormBody.Builder();
//        formBodyBuilder.add("Param name1", "Param value1");
//        formBodyBuilder.add("Param name2", "Param value2");
//        FormBody formBody= formBodyBuilder.build();
        EosStudentDto eosStudentDto = new EosStudentDto();
        eosStudentDto.setStudentName("fancky");
        eosStudentDto.setGrade("13");
        String jsonString = mapper.writeValueAsString(eosStudentDto);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = FormBody.create(jsonString, mediaType);
        String url = "http://localhost:8101/fileupload/addUser";
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody responseBody = Objects.requireNonNull(response.body());
            String result = Objects.requireNonNull(response.body()).string();

            return result;
        }
    }

    @PostMapping("/postMultipartFile")
    public String postMultipartFile() throws Exception {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        File file = new File("C:\\Users\\admin\\Desktop\\市场名单导入-测试.xls");
        String url = "http://localhost:8101/fileupload/uploadFileAndForm";

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("suggestion", "suggestion")
                .addFormDataPart("phone", "13956914410")
                //注意参数名称files 还是file  保持对应
                .addFormDataPart("files", file.getName(), RequestBody.create(file, mediaType))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody responseBody = Objects.requireNonNull(response.body());
            String result = Objects.requireNonNull(response.body()).string();

            return result;
        }

    }
}

