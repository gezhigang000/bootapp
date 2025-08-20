package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.admin.RootDeploymentInfo;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.StuckThreadDetectionHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.undertow.servlet.Servlets.defaultContainer;

public class ServletContainer {

     private Logger logger = LoggerFactory.getLogger(ServletContainer.class);

     public final Lock LOCK = new ReentrantLock();

     private final Lock stopLock = new ReentrantLock();

     private final Object monitor = new Object();

     private volatile Undertow server;
     private final String host;
     private final int port;
     private final String deploymentName = "admin";

     private boolean finished;
     private boolean forceReferenceCleanup;


     public ServletContainer(String host, int port) {
          this.host = host;
          this.port = port;
     }

     public void start() throws ServletException {
          final HttpHandler httpHandler = bootstrap();
          final StuckThreadDetectionHandler stuck = new StuckThreadDetectionHandler(100, httpHandler);
          final GracefulShutdownHandler shutdown = Handlers.gracefulShutdown(stuck);
          LOCK.lock();
          server = Undertow.builder()
                  .addHttpListener(port, host)
                  .setHandler(httpHandler)
                  .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                  .build();
          server.start();
          LOCK.unlock();
     }





     private HttpHandler bootstrap() throws ServletException {

          RootDeploymentInfo rootDeploymentInfo = new RootDeploymentInfo();
          rootDeploymentInfo
                  .setClassLoader(ServletContainer.class.getClassLoader())
                  .setContextPath("/")
                  .setResourceManager(new ClassPathResourceManager(ServletContainer.class.getClassLoader(), "webapp/resources"))
                  .addWelcomePage("index.html")
                  .setDeploymentName(deploymentName + "_" + "root");

          BootAppLoader.setDeploymentInfo(rootDeploymentInfo);
          WebApplicationContext webApplicationContext = defaultApplicationContext();
          InstanceFactory<DispatcherServlet> factory = new ImmediateInstanceFactory<>(new DispatcherServlet(webApplicationContext));
          rootDeploymentInfo.addServlet( new ServletInfo(  "AdminDispatcherServlet", DispatcherServlet.class, factory)
                  .addMapping("/admin/*")
                  .setLoadOnStartup(1)
                  .setAsyncSupported(true));
          final DeploymentManager manager = defaultContainer().addDeployment(rootDeploymentInfo);
          manager.deploy();
          final HttpHandler servletHandler = manager.start();
          final PathHandler pathHandler = Handlers.path(Handlers.redirect("/"))
                  .addPrefixPath("/", servletHandler);
          return pathHandler;
     }



     private WebApplicationContext defaultApplicationContext() {
          AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
          context.setConfigLocations("com.renlijia.bootapp.core.admin");
          return context;
     }

     protected void stop() throws Exception {
          stopLock.lock();
          try{
               server.stop();
               cleanupCaches();
               if (this.forceReferenceCleanup) {
                    forceReferenceCleanup();
               }
          }finally {
               stopLock.unlock();
          }
          System.gc();
          System.runFinalization();
     }

     private void cleanupCaches() {
          Introspector.flushCaches();
          cleanupKnownCaches();
     }

     private void cleanupKnownCaches() {
          // Whilst not strictly necessary it helps to clean up soft reference caches
          // early rather than waiting for memory limits to be reached
          ResolvableType.clearCache();
          cleanCachedIntrospectionResultsCache();
          ReflectionUtils.clearCache();
          clearAnnotationUtilsCache();
     }

     private void cleanCachedIntrospectionResultsCache() {
          clear(CachedIntrospectionResults.class, "acceptedClassLoaders");
          clear(CachedIntrospectionResults.class, "strongClassCache");
          clear(CachedIntrospectionResults.class, "softClassCache");
     }

     private void clearAnnotationUtilsCache() {
          try {
               AnnotationUtils.clearCache();
          }
          catch (Throwable ex) {
               clear(AnnotationUtils.class, "findAnnotationCache");
               clear(AnnotationUtils.class, "annotatedInterfaceCache");
          }
     }

     private void clear(Class<?> type, String fieldName) {
          try {
               Field field = type.getDeclaredField(fieldName);
               field.setAccessible(true);
               Object instance = field.get(null);
               if (instance instanceof Set) {
                    ((Set<?>) instance).clear();
               }
               if (instance instanceof Map) {
                    ((Map<?, ?>) instance).keySet().removeIf(this::isFromBootAppClassLoader);
               }
          }
          catch (Exception ex) {
               if (logger.isDebugEnabled()) {
                    logger.debug("Unable to clear field " + type + " " + fieldName, ex);
               }
          }
     }

     private boolean isFromBootAppClassLoader(Object object) {
          return (object instanceof Class && ((Class<?>) object).getClassLoader() instanceof BootAppClassloader);
     }


     private void forceReferenceCleanup() {
          try {
               final List<long[]> memory = new LinkedList<>();
               while (true) {
                    memory.add(new long[102400]);
               }
          }
          catch (OutOfMemoryError ex) {
               // Expected
          }
     }


     void finish() {
          synchronized (this.monitor) {
               if (!isFinished()) {
                    this.finished = true;
               }
          }
     }

     boolean isFinished() {
          synchronized (this.monitor) {
               return this.finished;
          }
     }

}
