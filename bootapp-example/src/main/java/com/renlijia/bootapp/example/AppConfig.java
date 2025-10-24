package com.renlijia.bootapp.example;


import com.renlijia.bootapp.core.SupportYamlPropertySourceFactory;
import com.renlijia.bootapp.example.controller.Custom2Filter;
import com.renlijia.bootapp.support.feign.EnableFeignClients;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@PropertySource(name = "application", value = {"classpath:application-${spring.profiles.active}.yml"}, encoding = "UTF-8",
        factory = SupportYamlPropertySourceFactory.class)
@EnableFeignClients(basePackages = {"com.dingtalent.ss.api", "com.renlijia.athena.api", "com.renlijia.crius.api", "com.dingtalent.salary.api.feign.self", "com.renlijia.pandora.api"})
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ServletComponentScan(basePackages = "com.renlijia.bootapp.example")
@ComponentScan(basePackages = {"com.renlijia.bootapp.example", "com.renlijia.bootapp.support.feign"})
public class AppConfig {

    @Bean
    public FilterRegistrationBean characterFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(characterEncodingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("characterEncodingFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean customFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(customFilter());
        registration.addUrlPatterns("/*");
        registration.setName("customFilter");
        registration.setOrder(2);
        System.out.println("create customFilterRegistration");
        return registration;
    }

    @Bean
    public FilterRegistrationBean custom3FilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(custom3Filter());
        registration.addUrlPatterns("/*");
        registration.setName("custom3Filter");
        registration.setOrder(3);
        System.out.println("create custom3FilterRegistration");
        return registration;
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public Filter customFilter() {
        CustomFilter customFilter = new CustomFilter();
        return customFilter;
    }

    @Bean
    public Filter custom2Filter() {
        Custom2Filter customFilter = new Custom2Filter();
        return customFilter;
    }

    @Bean
    public Filter custom3Filter() {
        Custom3Filter customFilter = new Custom3Filter();
        return customFilter;
    }


}
