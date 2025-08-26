package com.renlijia.bootapp.core.admin;

import com.renlijia.bootapp.core.HowInstall;

import java.net.URL;
import java.util.*;

public class AppJarHolder {

    private HowInstall howInstall;

    private  Set<AppJar> appJarSet = new HashSet<>();

    public AppJarHolder(){
    }

    public void loadJar(HowInstall howInstall){
        String appJarAbsolutePath = howInstall.appJarAbsolutePath();
        String includeByNameRegex = howInstall.appJarIncludeByNameRegex();
        String excludeByNameRegex = howInstall.appJareExcludeByNameRegex();
    }

    public  boolean registerAppJar(AppJar appJar){
       return appJarSet.add(appJar);
    }

    public  boolean removeAppJar(AppJar appJar){
        return appJarSet.remove(appJar);
    }

    public static class AppJar{
        public String jarName;
        public URL url;
        public Date lastUpdated;

        public AppJar(String jarName,URL url, Date  lastUpdated){
            this.jarName = jarName;
            this.url = url;
            this.lastUpdated = lastUpdated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppJar appJar = (AppJar) o;
            return Objects.equals(jarName, appJar.jarName) ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(jarName, url, lastUpdated);
        }
    }
}
