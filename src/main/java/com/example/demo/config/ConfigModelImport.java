package com.example.demo.config;

import com.example.demo.model.impot.ImportModelTest;
import com.example.demo.model.impot.MyImportSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * springboot2扫描的是META-INF/spring.factories文件
 * springboot3扫描的是META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.import
 *
 *
 * @Configuration+@Import 或加个注解@EnableSelector（组合注解）组合@Import
 *
 */
@Configuration
//@Import(ImportModelTest.class)

@Import(MyImportSelector.class)
public class ConfigModelImport {
/*
    "com.example.demo.model.impot.ImportModelTest",
        "importModelTest1",
        "importModelTest22"
 */

    @Bean
   public ImportModelTest importModelTest1()
   {
       return  new ImportModelTest();
   }

    @Bean("importModelTest22")
    public ImportModelTest importModelTest2()
    {
        return  new ImportModelTest();
    }
}
