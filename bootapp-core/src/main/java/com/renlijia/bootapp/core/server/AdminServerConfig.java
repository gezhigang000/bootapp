package com.renlijia.bootapp.core.server;

public class AdminServerConfig {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final String DEFAULT_CONTEXT_PATH = "/";
    private static final String DEFAULT_ADMIN_MAPPING = "/admin/*";

    private int port = DEFAULT_PORT;
    private String host = DEFAULT_HOST;
    private String contextPath = DEFAULT_CONTEXT_PATH;
    private String adminMapping = DEFAULT_ADMIN_MAPPING;
    private String[] springConfigLocations;
    private Class springRegisterClass;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getAdminMapping() {
        return adminMapping;
    }

    public void setAdminMapping(String adminMapping) {
        this.adminMapping = adminMapping;
    }

    public String[] getSpringConfigLocations() {
        return springConfigLocations;
    }

    public void setSpringConfigLocations(String[] springConfigLocations) {
        this.springConfigLocations = springConfigLocations;
    }

    public Class getSpringRegisterClass() {
        return springRegisterClass;
    }

    public void setSpringRegisterClass(Class springRegisterClass) {
        this.springRegisterClass = springRegisterClass;
    }
}
