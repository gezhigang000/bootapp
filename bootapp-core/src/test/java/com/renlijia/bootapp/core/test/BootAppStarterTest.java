package com.renlijia.bootapp.core.test;

import com.renlijia.bootapp.core.jetty.JettyAdminServer;
import com.renlijia.bootapp.core.server.AdminServerConfig;

public class BootAppStarterTest {

    public static void main(String[] args)  {

        AdminServerConfig adminServerConfig = new AdminServerConfig();
        adminServerConfig.setSpringConfigLocations(new String[]{"com.renlijia.bootapp.core.admin"});
        final JettyAdminServer server = new JettyAdminServer(adminServerConfig);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
