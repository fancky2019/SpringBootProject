package com.example.demo.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//HystrixDashboardConfig
@Configuration
public class HystrixDashboardConfig {

    @Bean
    public HystrixMetricsStreamServlet hystrixMetricsStreamServlet() {
        return new HystrixMetricsStreamServlet();
    }

    @Bean
    public ServletRegistrationBean registration(HystrixMetricsStreamServlet servlet) {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
