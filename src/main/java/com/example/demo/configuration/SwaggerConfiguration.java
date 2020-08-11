//package com.example.demo.configuration;
//
//import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
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
//@Configuration
//@EnableSwagger2
//@EnableKnife4j
//@Import(BeanValidatorPluginsConfiguration.class)
//public class SwaggerConfiguration {
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
//    @Bean(value = "defaultApi2")
//    public Docket defaultApi2() {
//        Docket docket=new Docket(DocumentationType.SWAGGER_2)
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
//        Contact contact = new Contact("王晓敏", "http://www.xiaomin2019.xyz", "18270789979@163.com");
//
//        return new ApiInfoBuilder()
//                .title("swagger-bootstrap-ui-demo RESTful APIs")
//                .description("# swagger-bootstrap-ui-demo RESTful APIs")
//                .termsOfServiceUrl("http://www.xx.com/")
//                .contact("xx@qq.com")
//                .version("1.0")
//                .build();
//    }
//
//
//}