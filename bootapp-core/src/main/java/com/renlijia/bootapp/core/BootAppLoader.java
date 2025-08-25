package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.jetty.JettyServer;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class BootAppLoader {

    public static DeploymentInfo deploymentInfo;

    private static Map<String,DispatcherServlet> dispatcherServletMap = new HashMap<>();

    public static DeploymentInfo getDeploymentInfo() {
        return deploymentInfo;
    }

    public static void setDeploymentInfo(DeploymentInfo deploymentInfo) {
        BootAppLoader.deploymentInfo = deploymentInfo;
    }

    public static ServletInfo buildServlet(String appJarPath, String dependenceJarPath) {
        BootAppConfig bootAppConfig = new BootAppConfig(appJarPath, dependenceJarPath);
        if (bootAppConfig.appJarFileList == null || bootAppConfig.appJarFileList.size() == 0) {
            return null;
        }

        List<URL> urlList = bootAppConfig.appJarFileList.stream().map(file -> {
            try {
                return file.toPath().toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        BootAppClassloader bootAppClassloader = new BootAppClassloader(urlList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
        List<AppMetaInfo> appMetaInfos = new ArrayList<>();
        ServiceLoader.load(AppMetaInfo.class, bootAppClassloader).forEach(appMetaInfos::add);
        if (appMetaInfos.size() != 1) {
            throw new RuntimeException("AppMetaInfo instances has and only has one");
        }
        AppMetaInfo appMetaInfo = appMetaInfos.get(0);
        WebApplicationContext webApplicationContext = buildApplicationContext(appMetaInfo, bootAppClassloader);
        InstanceFactory<DispatcherServlet> factory = new ImmediateInstanceFactory<>(new DispatcherServlet(webApplicationContext));
        String mapping = "/*";
        String servletName = "DispatcherServlet";
        if(appMetaInfo.appWebContext() != null){
            mapping = "/" + appMetaInfo.appWebContext() + "/*";
            servletName = appMetaInfo.appWebContext() + "DispatcherServlet";
        }
        return new ServletInfo(servletName, DispatcherServlet.class, factory)
                .addMapping(mapping)
                .setLoadOnStartup(1)
                .setAsyncSupported(true);
    }

    private static WebApplicationContext buildApplicationContext( AppMetaInfo appMetaInfo,  BootAppClassloader bootAppClassloader) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setClassLoader(bootAppClassloader);
        context.register(appMetaInfo.appClass());
        context.setConfigLocations(appMetaInfo.basePackages());
        return context;
    }

    public static void registerServlet(String appJarPath, String dependenceJarPath)  {
        BootAppConfig bootAppConfig = new BootAppConfig(appJarPath, dependenceJarPath);
        if (bootAppConfig.appJarFileList == null || bootAppConfig.appJarFileList.size() == 0) {
            return ;
        }

        List<URL> urlList = bootAppConfig.appJarFileList.stream().map(file -> {
            try {
                return file.toPath().toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        BootAppClassloader bootAppClassloader = new BootAppClassloader(urlList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
        List<AppMetaInfo> appMetaInfos = new ArrayList<>();
        ServiceLoader.load(AppMetaInfo.class, bootAppClassloader).forEach(appMetaInfos::add);
        if (appMetaInfos.size() != 1) {
            throw new RuntimeException("AppMetaInfo instances has and only has one");
        }
        AppMetaInfo appMetaInfo = appMetaInfos.get(0);
        WebApplicationContext webApplicationContext = buildApplicationContext(appMetaInfo, bootAppClassloader);
        String mapping = "/*";
        if(appMetaInfo.appWebContext() != null){
            mapping = "/" + appMetaInfo.appWebContext() + "/*";
        }
        DispatcherServlet  dispatcherServlet = new DispatcherServlet(webApplicationContext);

        JettyServer.registerServlet(appMetaInfo.appName(),mapping, dispatcherServlet);

    }
}
