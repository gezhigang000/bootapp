package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.jetty.JettyServer;
import jakarta.servlet.ServletException;

public class BootAppStarter {

//    public static void main(String[] args) throws ServletException {
//
//        final ServletContainer servletContainer = new ServletContainer("0.0.0.0",8080);
//        servletContainer.start();
//    }

    public static void main(String[] args) throws ServletException {

        final JettyServer server = new JettyServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
