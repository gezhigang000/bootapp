package com.renlijia.bootapp.core.admin.controller.data;

import com.renlijia.bootapp.core.app.AppJarHolder;

import java.text.SimpleDateFormat;

public class JarData {

    public String name;
    public String url;
    public String lastUpdated;
    private String loadDir;

    public JarData(AppJarHolder.AppJar appJar){
        if(appJar == null){
            return;
        }
        this.name = appJar.getName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.lastUpdated = simpleDateFormat.format(appJar.getLastUpdated());
        this.url = appJar.getUrl().toString();
        this.loadDir = appJar.getLoadDir();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLoadDir() {
        return loadDir;
    }

    public void setLoadDir(String loadDir) {
        this.loadDir = loadDir;
    }
}
