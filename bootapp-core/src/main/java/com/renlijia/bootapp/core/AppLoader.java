package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.jetty.JettyAdminServer;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AppLoader {

    private static WebApplicationContext buildApplicationContext(HowInstall howInstall, AppClassloader appClassloader) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setClassLoader(appClassloader);
        context.register(howInstall.appClass());
        context.setConfigLocations(howInstall.basePackages());
        return context;
    }

    public static void registerServlet(String appJarPath, String dependenceJarPath) {
        JarPath jarPath = new JarPath(appJarPath, dependenceJarPath);
        if (jarPath.appJarFileList == null || jarPath.appJarFileList.size() == 0) {
            return;
        }

        List<URL> urlList = jarPath.appJarFileList.stream().map(file -> {
            try {
                return file.toPath().toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        AppClassloader appClassloader = new AppClassloader(urlList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
        List<HowInstall> appMetaInfos = new ArrayList<>();
        ServiceLoader.load(HowInstall.class, appClassloader).forEach(appMetaInfos::add);
        if (appMetaInfos.size() != 1) {
            throw new RuntimeException("AppMetaInfo instances has and only has one");
        }
        HowInstall howInstall = appMetaInfos.get(0);
        WebApplicationContext webApplicationContext = buildApplicationContext(howInstall, appClassloader);
        String mapping = "/*";
        if (howInstall.appWebContext() != null) {
            mapping = "/" + howInstall.appWebContext() + "/*";
        }
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        JettyAdminServer.registerServlet(howInstall.appName(), mapping, dispatcherServlet);

    }
}
