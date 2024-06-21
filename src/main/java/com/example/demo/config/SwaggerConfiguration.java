//package com.example.demo.config;
//
////import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//
///*
//访问：
//springfox-swagger-ui:
//http://localhost:8081/swagger-ui.html
//knife4j:
//http://localhost:8081/doc.html
//
//*/
//
//@Configuration
//@EnableSwagger2
////@EnableKnife4j
//@Import(BeanValidatorPluginsConfiguration.class)
//@ConditionalOnProperty(value = {"knife4j.enable"}, matchIfMissing = true)
//public class SwaggerConfiguration {
//
//
//    @Bean(value = "defaultApi2")
//    public Docket defaultApi2() {
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                //分组名称
//                .groupName("2.X版本")
//                .select()
//                //这里指定Controller扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
//
//
//    private ApiInfo apiInfo() {
//        //联系方式
//        Contact contact = new Contact("fancky", "http://www.xiaomin2019.xyz", "18270789979@163.com");
//
//        return new ApiInfoBuilder()
//                .title("swagger-bootstrap-ui-demo RESTful APIs")
//                .description("# swagger-bootstrap-ui-demo RESTful APIs")
//                .termsOfServiceUrl("http://www.xx.com/")
//                .contact(contact)
//                .version("1.0")
//                .build();
//    }
//
//    /*
//    http://localhost:8081/v2/api-docs  看是否通
//        加入    registry.addResourceHandler("/swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//    */
//
//
////    public class WebMvcConfig extends WebMvcConfigurationSupport
////    /**
////     * 功能描述: 开放静态资源
////     */
////    @Override
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//////        "classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"
////        //重写这个方法，映射静态资源文件
////        registry.addResourceHandler("/**")
////                .addResourceLocations("classpath:/resources/")
////                .addResourceLocations("classpath:/static/")
////                .addResourceLocations("classpath:/pages/")
////        ;
////        registry.addResourceHandler("/swagger-ui.html")
////                .addResourceLocations("classpath:/META-INF/resources/");
////        registry.addResourceHandler("/webjars/**")
////                .addResourceLocations("classpath:/META-INF/resources/webjars/");
////        super.addResourceHandlers(registry);
////    }
////
////    @Override
////    public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/**")
////                .allowedOrigins("*")
////                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
////                .allowCredentials(true).maxAge(3600);
////    }
//
//}