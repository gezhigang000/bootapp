package cc.starapp.bootapp.example;


import cc.starapp.bootapp.core.SupportYamlPropertySourceFactory;
import cc.starapp.bootapp.example.controller.Custom2Filter;
import cc.starapp.bootapp.support.feign.EnableFeignClients;
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
@EnableFeignClients(basePackages = {"com.dingtalent.ss.api", "cc.starapp.athena.api", "cc.starapp.crius.api", "com.dingtalent.salary.api.feign.self", "cc.starapp.pandora.api"})
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ServletComponentScan(basePackages = "cc.starapp.bootapp.example")
@ComponentScan(basePackages = {"cc.starapp.bootapp.example", "cc.starapp.bootapp.support.feign"})
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
