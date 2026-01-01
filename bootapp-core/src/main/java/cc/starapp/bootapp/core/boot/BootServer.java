package cc.starapp.bootapp.core.boot;

import cc.starapp.bootapp.core.EmbeddedAppConfig;
import cc.starapp.bootapp.core.ServerConfig;
import cc.starapp.bootapp.core.admin.socket.DefaultJettyWebSocketServlet;
import cc.starapp.bootapp.core.app.AppJarHolder;
import cc.starapp.bootapp.core.app.AppRunMode;
import cc.starapp.bootapp.core.boot.jarfile.FileListener;
import cc.starapp.bootapp.core.boot.jarfile.FileListenerFactory;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class BootServer {

    private Logger logger = LoggerFactory.getLogger(BootServer.class);

    private Server server;

    private ServerConfig serverConfig;

    public BootServer(ServerConfig serverConfig){
        this.serverConfig = serverConfig;
    }

    public void start() throws Exception {
        server = new Server(createThreadPool());
        server.addConnector(createConnector());
        Handler handler = createAndStoreHandlers();
        loadBizJar();
        server.setHandler(handler);
        server.setStopAtShutdown(true);
        server.start();
        registerJarFileMonitor();
    }

    private void registerJarFileMonitor(){
        if(serverConfig.getRunMode() != AppRunMode.embedded) {
            return;
        }
        EmbeddedAppConfig embeddedAppConfig = serverConfig.getEmbeddedAppConfig();
        String[] libAbsoluteDirs = embeddedAppConfig.getLibAbsoluteDirs();
        for(String dir:libAbsoluteDirs) {
            File f = new File(dir);
            if(!f.exists()) {
               logger.warn("file monitor ignore dir:{}, dir not exists",dir);
            }
            if(!f.canRead()) {
                logger.warn("file monitor ignore dir:{}, dir can not read",dir);
            }
             FileListenerFactory.registerJarMonitor(f, new FileListener(), 3 * 1000);
            logger.info("file monitor register dir:{}",f);
        }
    }

    private void createSocket(ServletContextHandler servletContextHandler){
        JettyWebSocketServletContainerInitializer.configure(servletContextHandler, null);
        ServletHolder wsHolder = new ServletHolder("/ws/log", new DefaultJettyWebSocketServlet());
        servletContextHandler.addServlet(wsHolder, "/ws/log");

    }

    private void loadBizJar()throws Exception {
        AppJarHolder.init(serverConfig.getEmbeddedAppConfig());
        AppJarHolder.instance().reload();
    }

    private ThreadPool createThreadPool(){
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
        queuedThreadPool.setMaxThreads(serverConfig.getMaxThreads());
        queuedThreadPool.setMinThreads(serverConfig.getMinThreads());
        return queuedThreadPool;
    }

    private Connector createConnector(){
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setOutputBufferSize(serverConfig.getOutputBufferSize());
        httpConfiguration.setRequestHeaderSize(serverConfig.getRequestHeaderSize());
        httpConfiguration.setResponseHeaderSize(serverConfig.getResponseHeaderSize());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        ServerConnector serverConnector = new ServerConnector(server, serverConfig.getAcceptors()
                , serverConfig.getSelectors(), httpConnectionFactory);
        serverConnector.setHost(serverConfig.getHost());
        serverConnector.setPort(serverConfig.getPort());
        return serverConnector;
    }

    private Handler createAndStoreHandlers() {
        AnnotationConfigWebApplicationContext context = createBootSpringApplication();
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath(serverConfig.getContextPath());
        if(serverConfig.getRunMode() == AppRunMode.embedded) {
            DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
            servletContextHandler.addServlet(new ServletHolder("BootAdminServlet", dispatcherServlet), serverConfig.getAdminMapping());
            createSocket(servletContextHandler);
        }
        servletContextHandler.addEventListener(new ContextLoaderListener(context));
        servletContextHandler.addServletContainerInitializer(new JarAppInitializer());



        BootContext.instance().initServletContextHandler(servletContextHandler);
        MultipartConfigInjectionHandler multipartConfigInjectionHandler =
                new MultipartConfigInjectionHandler();
        multipartConfigInjectionHandler.setHandler(servletContextHandler);
        return multipartConfigInjectionHandler;
    }
    private AnnotationConfigWebApplicationContext createBootSpringApplication(){
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        if (serverConfig.getSpringConfigLocations() != null) {
            context.setConfigLocations(serverConfig.getSpringConfigLocations());
        }
        if (serverConfig.getSpringRegisterClass() != null) {
            context.register(serverConfig.getSpringRegisterClass());
        }
        if(serverConfig.getSpringProfileActive() != null) {
            System.setProperty("spring.profiles.active", serverConfig.getSpringProfileActive());
            context.getEnvironment().addActiveProfile(serverConfig.getSpringProfileActive());
        }
        BootContext.instance().initBootApplicationContext(context);
        BootContext.instance().setClassLoader(BootContext.class.getClassLoader());
        return context;
    }


}
