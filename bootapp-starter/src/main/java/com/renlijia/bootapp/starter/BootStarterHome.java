package com.renlijia.bootapp.starter;

import com.renlijia.bootapp.core.EmbeddedAppConfig;
import com.renlijia.bootapp.core.ServerConfig;
import com.renlijia.bootapp.core.app.AppRunMode;
import com.renlijia.bootapp.core.boot.BootServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public class BootStarterHome {

    private static Logger logger = LoggerFactory.getLogger(BootStarterHome.class);

    public static void main(String[] args) {

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        System.out.println("\n获取选项参数名称:");
        for(String optName : source.getPropertyNames()) {
            System.out.println(optName);
        }
        EmbeddedAppConfig embeddedAppConfig = new EmbeddedAppConfig();
        embeddedAppConfig.setLibAbsoluteDirs(new String[]{"/Users/gezhigang/code/bootapp/bootapp-example/target",
                "/Users/gezhigang/code/bootapp/bootapp-example-service/target"});
        ServerConfig serverConfig = new ServerConfig(AppRunMode.embedded, embeddedAppConfig);
        serverConfig.setSpringConfigLocations(new String[]{"com.renlijia.bootapp.core.admin"});
        serverConfig.setSpringProfileActive("daily");
        final BootServer server = new BootServer(serverConfig);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("bootapp start error",e);
        }
    }
}
