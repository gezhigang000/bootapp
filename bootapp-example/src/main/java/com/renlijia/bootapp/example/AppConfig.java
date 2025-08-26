package com.renlijia.bootapp.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ServletComponentScan(basePackages = "com.renlijia.bootapp.example")
@ComponentScan(basePackages = "com.renlijia.bootapp.example")
public class AppConfig {
}
