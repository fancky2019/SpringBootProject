package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/*
@EnableSwagger2  //EnableSwagger2  http://localhost:8081/swagger-ui.html


访问：
springfox-swagger-ui:
http://localhost:8081/swagger-ui.html
knife4j:
http://localhost:8081/doc.html


 */
// 新版swagger  	<dependency>
//			<groupId>org.springdoc</groupId>
//			<artifactId>springdoc-openapi-ui</artifactId>
//			<version>${springdoc.version}</version>
//		</dependency>

//@Configuration
//@EnableSwagger2
//@ConditionalOnProperty(prefix = "swagger2",value = {"enable"},havingValue = "true")
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //路径
                .apis(RequestHandlerSelectors.basePackage("com.example.demo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("···接口文档")
                .description("")
                .version("1.0")
                .build();
    }



    //springdoc-openapi-ui  配置
//    @Bean
//    public OpenAPI springShopOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("SpringShop API")
//                        .description("Spring shop sample application")
//                        .version("v0.0.1")
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringShop Wiki Documentation")
//                        .url("https://springshop.wiki.github.org/docs"));
//
//
//    }
}
