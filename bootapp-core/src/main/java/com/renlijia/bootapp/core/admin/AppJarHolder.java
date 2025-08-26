package com.renlijia.bootapp.core.admin;

import com.renlijia.bootapp.core.HowInstall;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppJarHolder {

    private  Set<AppJar> appJarSet = new HashSet<>();

    public void loadJar(HowInstall howInstall) throws MalformedURLException {
        String appJarAbsolutePath = howInstall.appJarAbsolutePath();
        String includeByNameRegex = howInstall.appJarIncludeByNameRegex();
        String excludeByNameRegex = howInstall.appJareExcludeByNameRegex();
        Pattern includePattern = null;
        Pattern excludePattern = null;
        if(includeByNameRegex != null && !includeByNameRegex.trim().isEmpty()){
            includePattern = Pattern.compile(includeByNameRegex.trim());
        }
        if(excludeByNameRegex != null && !excludeByNameRegex.trim().isEmpty()){
            excludePattern = Pattern.compile(excludeByNameRegex.trim());
        }

        File file = new File(appJarAbsolutePath);
        if (!file.exists()) {
            return;
        }
        if (!file.canRead()) {
            return;
        }
        File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            return;
        }
        for(File f:files){
            String name = f.getName();
            if(match(name,includePattern,excludePattern)){
                AppJar appJar = new AppJar(name,f.toURI().toURL(),f.lastModified());
                registerAppJar(appJar);
            }

        }
    }

    public boolean match(String fileName,Pattern includePattern,Pattern excludePattern){
        if(includePattern != null){
            Matcher includeMatcher = includePattern.matcher(fileName);
            if(includeMatcher.find()){
                if(excludePattern != null){
                    Matcher excludeMatcher = excludePattern.matcher(fileName);
                    if(excludeMatcher.find()){
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
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
        public long lastUpdated;

        public AppJar(String jarName,URL url, long  lastUpdated){
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
