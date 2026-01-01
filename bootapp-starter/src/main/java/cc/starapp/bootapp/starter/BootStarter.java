package cc.starapp.bootapp.starter;

import cc.starapp.bootapp.core.EmbeddedAppConfig;
import cc.starapp.bootapp.core.ServerConfig;
import cc.starapp.bootapp.core.app.AppRunMode;
import cc.starapp.bootapp.core.boot.BootServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public class BootStarter {

    private static Logger logger = LoggerFactory.getLogger(BootStarter.class);

    public static void main(String[] args) {

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        System.out.println("\n获取选项参数名称:");
        for(String optName : source.getPropertyNames()) {
            System.out.println(optName);
        }
        EmbeddedAppConfig embeddedAppConfig = new EmbeddedAppConfig();
        embeddedAppConfig.setLibAbsoluteDirs(new String[]{"/Users/well/code-github/bootapp/bootapp-example/target",
                "/Users/well/code-github/bootapp/bootapp-example-service/target"});
        ServerConfig serverConfig = new ServerConfig(AppRunMode.embedded, embeddedAppConfig);
        serverConfig.setSpringConfigLocations(new String[]{"cc.starapp.bootapp.core.admin"});
        serverConfig.setSpringProfileActive("daily");
        final BootServer server = new BootServer(serverConfig);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("bootapp start error",e);
        }
    }
}
