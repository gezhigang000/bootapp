package cc.starapp.bootapp.core.admin;

import cc.starapp.bootapp.core.SupportYamlPropertySourceFactory;
import freemarker.template.TemplateException;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;


@PropertySource(name = "bootapp" ,value={"classpath:bootapp-${spring.profiles.active}.yml"},factory= SupportYamlPropertySourceFactory.class)
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan(basePackages = "cc.starapp.bootapp.core")
@ServletComponentScan
public class AdminConfig   implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/adminstatic/**").addResourceLocations("classpath:/adminstatic/");
    }

    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html; charset=UTF-8");
        return resolver;
    }

    @Bean
    public FreeMarkerConfigurer getFreemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurer
                result = new FreeMarkerConfigurer();
        result.setTemplateLoaderPaths("classpath:/templates/");
        result.setDefaultEncoding("UTF-8");
        return result;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        DataSize maxFileSize = DataSize.ofMegabytes(10);
        //最大文件限制
        factory.setMaxFileSize(maxFileSize);
        //设置总上传数据总大小
        factory.setMaxRequestSize(maxFileSize);
        // 文件上传临时目录
//        String pathPreFix = appConfig.getPathPreFix();
//        if (StringUtils.isBlank(pathPreFix)) {
//            pathPreFix = TempFileUtils.getPathPreFix();
//        }
//        factory.setLocation(pathPreFix);
        return factory.createMultipartConfig();
    }



}
