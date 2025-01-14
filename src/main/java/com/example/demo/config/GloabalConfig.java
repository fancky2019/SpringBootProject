package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.Locale;

@Slf4j
@Configuration
public class GloabalConfig {

    @Bean
    public LocaleResolver localeResolver() {
        log.info("LocaleResolver loading...");
        SessionLocaleResolver slr = new SessionLocaleResolver();
        // 设置默认语言
        slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        // 设置默认语言
//        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
