package com.renlijia.bootapp.core.jetty;

import com.renlijia.bootapp.core.admin.AdminConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.*;
import java.util.stream.Collectors;

public class JettyServer {

    private static final int DEFAULT_PORT = 8080;
    private static final String CONTEXT_PATH = "/";
    private static final String MAPPING_URL = "/admin/*";
    private static Server server = new Server(DEFAULT_PORT);
    private static final ServletContextHandler servletContextHandler = new ServletContextHandler();

    private static Map<String, ServletHolder> servletHolderMap = new HashMap<>();

    public void start() throws Exception {
        server.setHandler(servletContextHandler(webApplicationContext()));
        server.start();
        server.join();
    }



    public static void registerServlet(String appName, String mapping, DispatcherServlet dispatcherServlet) {
        ServletHolder servletHolder = servletHolderMap.get(mapping);
        if (servletHolder != null) {
            try {
                servletHolder.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ServletMapping[] servletMappings = servletContextHandler.getServletHandler().getServletMappings();
            List<ServletMapping> servletMappingList = Arrays.asList(servletMappings);
            List<ServletMapping> newServletMapping = servletMappingList.stream().filter(e -> !mapping.equals(e.getPathSpecs()[0])).collect(Collectors.toList());
            servletContextHandler.getServletHandler().setServletMappings(newServletMapping.toArray(new ServletMapping[0]));
            ServletHolder[] servlets = servletContextHandler.getServletHandler().getServlets();
            List<ServletHolder> servletHolderList = Arrays.asList(servlets);
            List<ServletHolder> newServletHolderList = servletHolderList.stream().filter(e -> !appName.equals(e.getName())).collect(Collectors.toList());
            servletContextHandler.getServletHandler().setServlets(newServletHolderList.toArray(new ServletHolder[0]));

        }



        servletHolder = new ServletHolder(appName,dispatcherServlet);
        servletContextHandler.addServlet(servletHolder, mapping);
        servletHolderMap.put(mapping, servletHolder);
        servletContextHandler.addEventListener(new ContextLoaderListener(dispatcherServlet.getWebApplicationContext()));
    }


    private ServletContextHandler servletContextHandler(WebApplicationContext context) {
        servletContextHandler.setContextPath(CONTEXT_PATH);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        servletContextHandler.addServlet(new ServletHolder(dispatcherServlet), MAPPING_URL);
        servletContextHandler.addEventListener(new ContextLoaderListener(context));
        return servletContextHandler;
    }

    private WebApplicationContext webApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.renlijia.bootapp.core.admin");
        return context;
    }
}
