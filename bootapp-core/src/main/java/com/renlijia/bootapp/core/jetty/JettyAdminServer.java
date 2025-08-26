package com.renlijia.bootapp.core.jetty;

import com.renlijia.bootapp.core.ApplicationContextBuilder;
import com.renlijia.bootapp.core.admin.AppJarHolder;
import com.renlijia.bootapp.core.server.AdminServer;
import com.renlijia.bootapp.core.server.AdminServerConfig;
import com.renlijia.bootapp.core.server.RunMode;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.net.MalformedURLException;
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
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
        queuedThreadPool.setMaxThreads(adminServerConfig.getMaxThreads());
        queuedThreadPool.setMinThreads(adminServerConfig.getMinThreads());
        server = new Server(queuedThreadPool);
        ServerConnector serverConnector = getServerConnector();
        serverConnector.setHost(adminServerConfig.getHost());
        serverConnector.setPort(adminServerConfig.getPort());
        server.addConnector(serverConnector);
        if (adminServerConfig.getRunMode() == RunMode.embedded) {
            server.setHandler(servletContextHandler(buildRootContext()));
            //安装app
            installEmbeddedApp();
        } else {
            installStandaloneApp();
        }
        server.start();
        server.join();
    }

    private void installEmbeddedApp() throws MalformedURLException {
        AppJarHolder.setEmbeddedAppConfig(adminServerConfig.getEmbeddedAppConfig());
        AppJarHolder.reload();
        WebApplicationContext webApplicationContext = ApplicationContextBuilder.buildAppContext(AppJarHolder.getHowInstall(), AppJarHolder.getAppClassloader());
        String mapping = "/*";
        if (AppJarHolder.getHowInstall().appWebContext() != null) {
            mapping = "/" + AppJarHolder.getHowInstall().appWebContext() + "/*";
        }
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        registerServlet(AppJarHolder.getHowInstall().appName(), mapping, dispatcherServlet);
    }

    public static void reInstall() throws MalformedURLException {
        AppJarHolder.reload();
        WebApplicationContext webApplicationContext = ApplicationContextBuilder.buildAppContext(AppJarHolder.getHowInstall(), AppJarHolder.getAppClassloader());
        String mapping = "/*";
        if (AppJarHolder.getHowInstall().appWebContext() != null) {
            mapping = "/" + AppJarHolder.getHowInstall().appWebContext() + "/*";
        }
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        registerServlet(AppJarHolder.getHowInstall().appName(), mapping, dispatcherServlet);
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }


    private ServerConnector getServerConnector() {
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setOutputBufferSize(adminServerConfig.getOutputBufferSize());
        httpConfiguration.setRequestHeaderSize(adminServerConfig.getRequestHeaderSize());
        httpConfiguration.setResponseHeaderSize(adminServerConfig.getResponseHeaderSize());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

        ServerConnector serverConnector = new ServerConnector(server, adminServerConfig.getAcceptors()
                , adminServerConfig.getSelectors(), httpConnectionFactory);
        return serverConnector;
    }


    private static void registerServlet(String appName, String mapping, DispatcherServlet dispatcherServlet) {
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
    }


    private ServletContextHandler servletContextHandler(WebApplicationContext context) {
        servletContextHandler.setContextPath(adminServerConfig.getContextPath());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        servletContextHandler.addServlet(new ServletHolder(dispatcherServlet), adminServerConfig.getAdminMapping());
        servletContextHandler.addEventListener(new ContextLoaderListener(context));
        return servletContextHandler;
    }

    private void installStandaloneApp() throws MalformedURLException {
        servletContextHandler.setContextPath(adminServerConfig.getContextPath());

        AppJarHolder.setEmbeddedAppConfig(adminServerConfig.getEmbeddedAppConfig());
        AppJarHolder.reload();
        WebApplicationContext webApplicationContext = ApplicationContextBuilder.buildAppContext(AppJarHolder.getHowInstall(), AppJarHolder.getAppClassloader());
        String mapping = "/*";
        if (AppJarHolder.getHowInstall().appWebContext() != null) {
            mapping = "/" + AppJarHolder.getHowInstall().appWebContext() + "/*";
        }
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        registerServlet(AppJarHolder.getHowInstall().appName(), mapping, dispatcherServlet);

        servletContextHandler.addEventListener(new ContextLoaderListener(webApplicationContext));
    }

    private WebApplicationContext buildRootContext() {
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
