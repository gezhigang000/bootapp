package com.renlijia.bootapp.core;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class ApplicationContextBuilder {


    public static WebApplicationContext buildAppContext(HowInstall howInstall, AppClassloader appClassloader) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setClassLoader(appClassloader);
        context.register(howInstall.appClass());
        context.setConfigLocations(howInstall.basePackages());
        return context;
    }

}
