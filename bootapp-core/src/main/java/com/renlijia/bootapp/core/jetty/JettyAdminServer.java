package com.renlijia.bootapp.core.jetty;

import com.renlijia.bootapp.core.server.AdminServer;
import com.renlijia.bootapp.core.server.AdminServerConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JettyAdminServer implements AdminServer {

    private static Server server;
    private AdminServerConfig adminServerConfig;

    public JettyAdminServer(AdminServerConfig adminServerConfig) {
        if (adminServerConfig == null) {
            throw new RuntimeException("AdminServerConfig can not null");
        }
        this.adminServerConfig = adminServerConfig;
    }


    private static final ServletContextHandler servletContextHandler = new ServletContextHandler();

    private static Map<String, ServletHolder> servletHolderMap = new HashMap<>();

    public void start() throws Exception {
        server = new Server(new InetSocketAddress(adminServerConfig.getHost(), adminServerConfig.getPort()));
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
        servletHolder = new ServletHolder(appName, dispatcherServlet);
        servletContextHandler.addServlet(servletHolder, mapping);
        servletHolderMap.put(mapping, servletHolder);
        servletContextHandler.addEventListener(new ContextLoaderListener(dispatcherServlet.getWebApplicationContext()));
    }


    private ServletContextHandler servletContextHandler(WebApplicationContext context) {
        servletContextHandler.setContextPath(adminServerConfig.getContextPath());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        servletContextHandler.addServlet(new ServletHolder(dispatcherServlet), adminServerConfig.getAdminMapping());
        servletContextHandler.addEventListener(new ContextLoaderListener(context));
        return servletContextHandler;
    }

    private WebApplicationContext webApplicationContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        if (adminServerConfig.getSpringConfigLocations() != null) {
            context.setConfigLocations(adminServerConfig.getSpringConfigLocations());
        }
        if (adminServerConfig.getSpringRegisterClass() != null) {
            context.register(adminServerConfig.getSpringRegisterClass());
        }
        return context;
    }
}
