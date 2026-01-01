package cc.starapp.bootapp.core.boot;

import cc.starapp.bootapp.core.app.AppJarHolder;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Set;

public class JarAppInitializer implements ServletContainerInitializer {

    private Logger log = LoggerFactory.getLogger(JarAppInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        log.info("JarAppInitializer startup ....");
        if(AppJarHolder.instance().getHowInstall() == null){
            log.info("installEmbeddedApp fail , how install is null ");
            return;
        }

        WebApplicationContext webApplicationContext = BootContext.instance().buildDynamicSpringAppContext();
        DispatcherServlet dispatcherServlet = BootContext.instance().buildDispatcherServlet(webApplicationContext);
        BootContext.instance().registerServlet(dispatcherServlet);
        log.info("JarAppInitializer end ....");
    }




}
