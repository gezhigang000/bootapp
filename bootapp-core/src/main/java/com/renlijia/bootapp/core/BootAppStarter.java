package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.jetty.JettyServer;

public class BootAppStarter {

    public static void main(String[] args)  {

        final JettyServer server = new JettyServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
