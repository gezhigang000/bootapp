package com.renlijia.bootapp.core;

import com.renlijia.bootapp.core.app.AppRunMode;

public class ServerConfig {

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
    private String springProfileActive;

    private AppRunMode appRunMode = AppRunMode.standalone;
    private EmbeddedAppConfig embeddedAppConfig;

    private int minThreads = 100;
    private int maxThreads = 500;

    private int acceptors = -1;
    private int selectors = -1;

    private int outputBufferSize = 32 * 1024;
    ;
    private int requestHeaderSize = 8192;
    private int responseHeaderSize = 8192;

    public ServerConfig() {

    }

    public ServerConfig(AppRunMode appRunMode, EmbeddedAppConfig embeddedAppConfig) {
        this.appRunMode = appRunMode;
        this.embeddedAppConfig = embeddedAppConfig;
        if (embeddedAppConfig == null || embeddedAppConfig.getLibAbsoluteDirs() == null) {
            throw new RuntimeException("embedded mode must set app lib absolute dir");
        }

    }

    public String getSpringProfileActive() {
        return springProfileActive;
    }

    public void setSpringProfileActive(String springProfileActive) {
        this.springProfileActive = springProfileActive;
    }

    public AppRunMode getRunMode() {
        return appRunMode;
    }

    public EmbeddedAppConfig getEmbeddedAppConfig() {
        return embeddedAppConfig;
    }

    public void setEmbeddedAppConfig(EmbeddedAppConfig embeddedAppConfig) {
        this.embeddedAppConfig = embeddedAppConfig;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    public int getSelectors() {
        return selectors;
    }

    public void setSelectors(int selectors) {
        this.selectors = selectors;
    }

    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    public int getRequestHeaderSize() {
        return requestHeaderSize;
    }

    public void setRequestHeaderSize(int requestHeaderSize) {
        this.requestHeaderSize = requestHeaderSize;
    }

    public int getResponseHeaderSize() {
        return responseHeaderSize;
    }

    public void setResponseHeaderSize(int responseHeaderSize) {
        this.responseHeaderSize = responseHeaderSize;
    }

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
