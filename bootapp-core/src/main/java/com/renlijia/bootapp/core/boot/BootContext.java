package com.renlijia.bootapp.core.boot;

import com.renlijia.bootapp.core.EmbeddedApplicationContext;
import com.renlijia.bootapp.core.HowInstall;
import com.renlijia.bootapp.core.app.AppJarHolder;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.eclipse.jetty.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class BootContext {

    private Logger logger = LoggerFactory.getLogger(BootContext.class);

    private static final BootContext _instance = new BootContext();

    public static BootContext instance() {
        return _instance;
    }

    private BootContext() {

    }

    private ServletContextHandler servletContextHandler;

    private ApplicationContext bootApplicationContext;

    private Map<String, ServletHolder> dynamicServletHolderMap = new HashMap<>();

    private Map<String, FilterHolder> dynamicFilterHolderMap = new HashMap<>();
    private Map<String, FilterMapping> dynamicFilterMappingMap = new HashMap<>();

    private List<EventListener> dynamicEventListener = new ArrayList<>();

    private  ClassLoader classLoader ;

    private static ReentrantLock lock = new ReentrantLock();
    private volatile boolean loading = false;

    public boolean reloadDynamicApp() throws Exception {
        if(lock.tryLock(1, TimeUnit.SECONDS)) {
            if(loading){
                logger.info("load is running ignore..");
                return true;
            }
            loading = true;
            try {
                boolean reload = AppJarHolder.instance().reload();
                if(!reload){
                    logger.warn("reload app jar false....");
                    loading = false;
                    return false;
                }
                destroyDynamicServeltComponent();
                WebApplicationContext webApplicationContext = buildDynamicSpringAppContext();
                DispatcherServlet dispatcherServlet = buildDispatcherServlet(webApplicationContext);
                registerServlet(dispatcherServlet);
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean isLoading(){
        return loading;
    }

    public void resetLoading(){
         loading = false;
    }

    public WebApplicationContext buildDynamicSpringAppContext() {
        EmbeddedApplicationContext context = new EmbeddedApplicationContext();
        String[] activeProfiles = bootApplicationContext.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            context.getEnvironment().setActiveProfiles(profile);
        }
        HowInstall howInstall = AppJarHolder.instance().getHowInstall();
        if (howInstall.basePackages() != null) {
            context.setConfigLocations(howInstall.basePackages());
        }
        context.setClassLoader(AppJarHolder.instance().getAppClassloader());
        context.register(howInstall.appClass());
        return context;
    }

    public DispatcherServlet buildDispatcherServlet(WebApplicationContext webApplicationContext) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        return dispatcherServlet;
    }

    public void registerFilter(Map<String, FilterHolder> paramDynamicFilterHolderMap,Map<String, FilterMapping> paramDynamicFilterMappingMap) {
        servletContextHandler.getServletHandler().setFilters(paramDynamicFilterHolderMap.values().toArray(new FilterHolder[0]));
        dynamicFilterHolderMap.putAll(paramDynamicFilterHolderMap);
        servletContextHandler.getServletHandler().setFilterMappings(paramDynamicFilterMappingMap.values().toArray(new FilterMapping[0]));
        dynamicFilterMappingMap.putAll(paramDynamicFilterMappingMap);
    }


    public void registerEventListener(EventListener eventListener) {
        if (eventListener == null) {
            return;
        }
        servletContextHandler.addEventListener(eventListener);
        dynamicEventListener.add(eventListener);

    }

    public void registerServlet(DispatcherServlet dispatcherServlet) {
        String mapping = buildMappingName();
        ServletHolder servletHolder = new ServletHolder(AppJarHolder.instance().getHowInstall().appName(), dispatcherServlet);
        servletContextHandler.addServlet(servletHolder, mapping);
        dynamicServletHolderMap.put(mapping, servletHolder);
        checkState(servletHolder);
    }

    private void checkState(final ServletHolder servletHolder){
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                while(!servletHolder.isAvailable()){
                    try {
                        Thread.sleep(1*1000);
                    } catch (InterruptedException e) {

                    }
                }
                BootContext.instance().resetLoading();

            }
        });
        thread.start();
    }

    private String buildMappingName() {
        String mapping = "/*";
        if (AppJarHolder.instance().getHowInstall().appWebContext() != null) {
            mapping = "/" + AppJarHolder.instance().getHowInstall().appWebContext() + "/*";
        }
        return mapping;
    }

    public void destroyDynamicServeltComponent() {
        logger.info(" start destroy dynamic component...");
        Iterator<Map.Entry<String, ServletHolder>> servletHolderIterator = dynamicServletHolderMap.entrySet().iterator();
        while (servletHolderIterator.hasNext()) {
            Map.Entry<String, ServletHolder> next = servletHolderIterator.next();
            String mapping = next.getKey();
            ServletHolder servletHolder = next.getValue();
            logger.info(" start destroy dynamic servlet mapping: {} component:{}...", mapping, servletHolder);
            try {
                servletHolder.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            getServletContextHandler().removeBean(servletHolder);
            ServletMapping[] servletMappings = getServletContextHandler().getServletHandler().getServletMappings();
            List<ServletMapping> servletMappingList = Arrays.asList(servletMappings);
            List<ServletMapping> newServletMapping = servletMappingList.stream().filter(e -> !mapping.equals(e.getPathSpecs()[0])).collect(Collectors.toList());
            getServletContextHandler().getServletHandler().setServletMappings(newServletMapping.toArray(new ServletMapping[0]));
            ServletHolder[] servlets = getServletContextHandler().getServletHandler().getServlets();
            List<ServletHolder> servletHolderList = Arrays.asList(servlets);
            List<ServletHolder> newServletHolderList = servletHolderList.stream().filter(e -> !AppJarHolder.instance().getHowInstall().appName().equals(e.getName())).collect(Collectors.toList());
            getServletContextHandler().getServletHandler().setServlets(newServletHolderList.toArray(new ServletHolder[0]));
        }
        dynamicServletHolderMap.clear();

        Iterator<FilterHolder> iterator = dynamicFilterHolderMap.values().iterator();
        while (iterator.hasNext()) {
            FilterHolder next = iterator.next();
            logger.info("destroy FilterHolder {}", next);
            try {
                next.doStop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            servletContextHandler.getServletHandler().removeFilterHolder(next);
        }
        dynamicFilterHolderMap.clear();

        for (EventListener eventListener : dynamicEventListener) {
            logger.info(" destroy EventListener {}", eventListener);
            servletContextHandler.getServletHandler().removeEventListener(eventListener);
        }
        dynamicEventListener.clear();

    }

    public ServletContextHandler getServletContextHandler() {
        return servletContextHandler;
    }

    protected void initServletContextHandler(ServletContextHandler servletContextHandler) {
        this.servletContextHandler = servletContextHandler;
    }

    public ApplicationContext getBootApplicationContext() {
        return bootApplicationContext;
    }

    protected void initBootApplicationContext(ApplicationContext bootApplicationContext) {
        this.bootApplicationContext = bootApplicationContext;
    }

    public Map<String, ServletHolder> dynamicServletHolderMap() {
        return dynamicServletHolderMap;
    }


    public Map<String, FilterHolder> dynamicFilterHolderMap() {
        return dynamicFilterHolderMap;
    }


}
