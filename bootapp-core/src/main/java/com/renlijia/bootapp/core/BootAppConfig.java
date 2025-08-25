package com.renlijia.bootapp.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BootAppConfig {

    public String appName;

    public String appJarPath;

    public String dependenceJarPath;

    public List<File> appJarFileList;

    public List<File> dependenceJarFileList;

    public Set<String> scanPackages;

    public String appWebContext;

    public BootAppConfig(String appJarPath, String dependenceJarPath) {
        this.appJarPath = appJarPath;
        this.dependenceJarPath = dependenceJarPath;
        initJarFile();
        initDependenceJarFile();
    }

    private void initJarFile() {
        if (appJarPath == null) {
            return;
        }
        File file = new File(appJarPath);
        if (!file.exists()) {
            return;
        }
        if (!file.canRead()) {
            return;
        }
        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files != null && files.length > 0) {
            appJarFileList = Arrays.stream(files).collect(Collectors.toList());
        }
    }

    private void initDependenceJarFile() {
        if (dependenceJarPath == null) {
            return;
        }
        File file = new File(dependenceJarPath);
        if (!file.exists()) {
            return;
        }
        if (!file.canRead()) {
            return;
        }
        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files != null && files.length > 0) {
            dependenceJarFileList = Arrays.stream(files).collect(Collectors.toList());
        }
    }

}
