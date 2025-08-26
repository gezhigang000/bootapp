package com.renlijia.bootapp.core.test;

import com.renlijia.bootapp.core.EmbeddedAppConfig;
import com.renlijia.bootapp.core.jetty.JettyAdminServer;
import com.renlijia.bootapp.core.server.AdminServerConfig;
import com.renlijia.bootapp.core.server.AppRunMode;

public class BootAppStarterTest {

    public static void main(String[] args)  {

        EmbeddedAppConfig embeddedAppConfig = new EmbeddedAppConfig();
        embeddedAppConfig.setLibAbsoluteDirs(new String[]{"/Users/well/code-gitee/bootapp/bootapp-example/target"});
        AdminServerConfig adminServerConfig = new AdminServerConfig(AppRunMode.embedded, embeddedAppConfig);
        adminServerConfig.setSpringConfigLocations(new String[]{"com.renlijia.bootapp.core.admin"});
        final JettyAdminServer server = new JettyAdminServer(adminServerConfig);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
