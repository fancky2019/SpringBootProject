package com.example.demo.jobs.xxljob;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.init.ApplicationRunnerImp;
import com.example.demo.model.pojo.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * clone 官网项目  启动admin 项目 ，注意配置mysql
 * 增删改查 JobInfoController
 * 登录再
 *
 *
 * 调度中心（xxl-job-admin）与执行器 xxl-job-executor
 */
@Component
@Order(10)
public class XXLJobApplicationRunnerImp implements ApplicationRunner {
    private static Logger LOGGER = LogManager.getLogger(ApplicationRunnerImp.class);


    private static final String ADD_JOB = "/jobinfo/add";
    private static final String UPDATE_URL = "/jobinfo/update";
    private static final String REMOVE_URL = "/jobinfo/remove";
    private static final String PAUSE_URL = "/jobinfo/stop";
    private static final String START_JOB = "/jobinfo/start";

    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${config.configmodel.fist-Name}")
    private String fistName;

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;


    private String userName = "admin";


    private String password = "123456";


    public OkHttpClient okHttpClient() {
        return okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间,默认10s
                .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String name = fistName;
        LOGGER.info("ApplicationRunnerImp");


//        addXxlJob();

        okHttpClient();
        loginAdmin();
        int jobId = addJob();
        startJob(jobId);
    }

    public void loginAdmin() {
        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("userName", "admin")
                    .add("password", "123456")
                    .build();

//            MediaType mediaType = MediaType.parse("x-www-form-urlencoded");
//            RequestBody requestBody = FormBody.create(jobJson, mediaType);
            String url = adminAddresses + "/login";
            Request request = new Request.Builder()
                    // .addHeader("XXL-JOB-ACCESS-TOKEN", "default_token")
                    .url(url)
                    .post(requestBody)
                    .build();


            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody responseBody = Objects.requireNonNull(response.body());
                String result = Objects.requireNonNull(response.body()).string();
                int m = 0;
            }

        } catch (Exception ex) {
            String str = ex.getMessage();
            int m = 0;
        }
    }


    public void startJob() {

        try {


            RequestBody requestBody = new FormBody.Builder()
                    .add("jobGroup", "2")
                    .add("jobDesc", "代码启动任务")
                    .add("author", "fancky")
                    .add("glueType", "BEAN")
                    .add("executorHandler", "dynamicJob")
                    .add("scheduleType", "CRON")
                    .add("scheduleConf", "0/15 * * * * ?")
                    .add("executorRouteStrategy", "FIRST")
                    .add("misfireStrategy", "DO_NOTHING")
                    .add("executorBlockStrategy", "SERIAL_EXECUTION")
                    .build();

//        MediaType mediaType = MediaType.parse("x-www-form-urlencoded");
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String url = adminAddresses + ADD_JOB;
            Request request = new Request.Builder()
                    // .addHeader("XXL-JOB-ACCESS-TOKEN", "default_token")
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody responseBody = Objects.requireNonNull(response.body());
                String result = Objects.requireNonNull(response.body()).string();

                int m = 0;

            }


            int m = 0;
        } catch (Exception ex) {
            String str = ex.getMessage();
            int m = 0;
        }

    }


    public int addJob() {

        try {

            Student student = new Student();
            student.setName("fancky");
            student.setAge(27);
            String executorParamJson = objectMapper.writeValueAsString(student);


            RequestBody requestBody = new FormBody.Builder()
                    .add("jobGroup", "2")
                    .add("jobDesc", "代码启动任务")
                    .add("author", "fancky")
                    .add("glueType", "BEAN")
                    .add("executorHandler", "dynamicJob")
                    .add("scheduleType", "CRON")
                    .add("scheduleConf", "0/15 * * * * ?")
                    .add("executorRouteStrategy", "FIRST")
                    .add("misfireStrategy", "DO_NOTHING")
                    .add("executorBlockStrategy", "SERIAL_EXECUTION")
                    .add("executorParam", executorParamJson)
                    .build();

//        MediaType mediaType = MediaType.parse("x-www-form-urlencoded");
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String url = adminAddresses + ADD_JOB;
            Request request = new Request.Builder()
                    // .addHeader("XXL-JOB-ACCESS-TOKEN", "default_token")
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody responseBody = Objects.requireNonNull(response.body());
                //{"code":200,"msg":null,"content":"11"}:content返回新增的id
                String result = Objects.requireNonNull(response.body()).string();

                HashMap<String, String> resultMap = objectMapper.readValue(result, new TypeReference<HashMap<String, String>>() {
                });
                String jobId = resultMap.get("content");

                return Integer.parseInt(jobId);

            }


        } catch (Exception ex) {
            String str = ex.getMessage();
            int m = 0;
            return 0;
        }

    }

    public void startJob(int jobId) {

        try {


            RequestBody requestBody = new FormBody.Builder()
                    .add("id", String.valueOf(jobId))
                    .build();

//        MediaType mediaType = MediaType.parse("x-www-form-urlencoded");
//        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            String url = adminAddresses + START_JOB;
            Request request = new Request.Builder()
                    // .addHeader("XXL-JOB-ACCESS-TOKEN", "default_token")
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody responseBody = Objects.requireNonNull(response.body());
                //{"code":200,"msg":null,"content":null}
                String result = Objects.requireNonNull(response.body()).string();

                HashMap<String, String> resultMap = objectMapper.readValue(result, new TypeReference<HashMap<String, String>>() {
                });

                int m = 0;
            }


        } catch (Exception ex) {
            String str = ex.getMessage();
            int m = 0;

        }

    }


    private String cookie;

    public int addXxlJob() throws JsonProcessingException {
        String path = "http://127.0.0.1:8181/xxl-job-admin" + "/jobinfo/add";
        Student student = new Student();
        student.setName("fancky");
        student.setAge(27);
        String executorParamJson = objectMapper.writeValueAsString(student);
        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("jobGroup", "2");
        paramMap.put("jobDesc", "代码启动任务");
        paramMap.put("author", "fancky");
        paramMap.put("glueType", "BEAN");
        paramMap.put("executorHandler", "dynamicJob");
        paramMap.put("scheduleType", "CRON");
        paramMap.put("scheduleConf", "0/15 * * * * ?");
        paramMap.put("executorRouteStrategy", "FIRST");
        paramMap.put("misfireStrategy", "DO_NOTHING");
        paramMap.put("executorBlockStrategy", "SERIAL_EXECUTION");
        paramMap.put("executorParam", executorParamJson);
        if (StringUtils.isBlank(cookie)) {
            cookie = getCookie();
        }

        //依赖 hutool-all
        HttpResponse response = HttpRequest.post(path).form(paramMap).execute();

        if (HttpStatus.HTTP_OK != response.getStatus()) {
            // TODO
        }
        JSONObject jsonObject = JSON.parseObject(response.body());
        int jobId = jsonObject.getIntValue("content");
        return jobId;

    }

    public String getCookie() {
        String path = "http://127.0.0.1:8181/xxl-job-admin" + "/login";
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("userName", "admin");
        hashMap.put("password", "123456");
        HttpResponse response = HttpRequest.post(path).form(hashMap).execute();
        List<HttpCookie> cookies = response.getCookies();
        StringBuilder sb = new StringBuilder();
        for (HttpCookie cookie : cookies) {
            sb.append(cookie.toString());
        }
        String cookie = sb.toString();
        return cookie;


    }


//    @XxlJob("dynamicJob")
//    public void dynamicJob(String param) throws Exception {
//        // XxlJobLogger.log("bean method jobhandler running...");
//        XxlJobHelper.log("dynamicJob");
//        LOGGER.info("BeanMethodJobHandler");
//
//    }

}
