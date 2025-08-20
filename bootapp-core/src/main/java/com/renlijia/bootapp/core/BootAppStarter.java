package com.renlijia.bootapp.core;

import jakarta.servlet.ServletException;

public class BootAppStarter {

    public static void main(String[] args) throws ServletException {

        final ServletContainer servletContainer = new ServletContainer("0.0.0.0",8080);
        servletContainer.start();
    }
}
