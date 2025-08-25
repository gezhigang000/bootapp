package com.renlijia.bootapp.core.admin;

import java.net.URL;
import java.util.*;

public class JarFactory {

    private static Set<AppJar> appJarSet = new HashSet<>();


    public void loadJar(String jarPath,String jarNameRegex){

    }

    public static boolean registerAppJar(AppJar appJar){
       return appJarSet.add(appJar);
    }

    public static boolean removeAppJar(AppJar appJar){
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
