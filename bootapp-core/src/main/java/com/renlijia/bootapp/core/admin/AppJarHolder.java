package com.renlijia.bootapp.core.admin;

import com.renlijia.bootapp.core.AppClassloader;
import com.renlijia.bootapp.core.EmbeddedAppConfig;
import com.renlijia.bootapp.core.HowInstall;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppJarHolder {

    private static Set<AppJar> appJarSet = new TreeSet<>();

    private static HowInstall howInstall;

    private static EmbeddedAppConfig embeddedAppConfig;

    private static AppClassloader appClassloader;


    public static List<AppJar> getAppJarList() {
        return appJarSet.stream().sorted(Comparator.comparing(o -> o.name)).collect(Collectors.toList());
    }

    public static void setEmbeddedAppConfig(EmbeddedAppConfig embeddedAppConfig) {
        AppJarHolder.embeddedAppConfig = embeddedAppConfig;
    }

    private static void clean(){
        appJarSet = new HashSet<>();
        howInstall = null;
    }

    public static void reload() throws MalformedURLException {
        clean();
        Pattern includePattern = null;
        Pattern excludePattern = null;
        if (embeddedAppConfig.getIncludeByNameRegex() != null && !embeddedAppConfig.getIncludeByNameRegex().trim().isEmpty()) {
            includePattern = Pattern.compile(embeddedAppConfig.getIncludeByNameRegex().trim());
        }
        if (embeddedAppConfig.getExcludeByNameRegex() != null && !embeddedAppConfig.getExcludeByNameRegex().trim().isEmpty()) {
            excludePattern = Pattern.compile(embeddedAppConfig.getExcludeByNameRegex().trim());
        }

        for (String jarDir : embeddedAppConfig.getLibAbsoluteDirs()) {
            File file = new File(jarDir);
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
            for (File f : files) {
                String name = f.getName();
                if (match(name, includePattern, excludePattern)) {
                    AppJar appJar = new AppJar(name, f.toURI().toURL(), f.lastModified());
                    appJarSet.add(appJar);
                }
            }
        }
        loadHowInstall();
    }

    private static void loadHowInstall() {
        List<URL> urlList = appJarSet.stream().map(jar -> jar.url).collect(Collectors.toList());

        appClassloader = new AppClassloader(urlList.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());
        List<HowInstall> howInstalls = new ArrayList<>();
        ServiceLoader.load(HowInstall.class, appClassloader).forEach(howInstalls::add);
        if (howInstalls.size() != 1) {
            throw new RuntimeException("HowInstall instances has and only has one");
        }
        howInstall = howInstalls.get(0);
    }

    public static AppClassloader getAppClassloader() {
        return appClassloader;
    }

    public static HowInstall getHowInstall() {
        return howInstall;
    }

    public static boolean match(String fileName, Pattern includePattern, Pattern excludePattern) {
        if (isInclude(fileName, includePattern)) {
            if (isExclude(fileName, excludePattern)) {
                return false;
            }
            return true;
        }
        if (isExclude(fileName, excludePattern)) {
            return false;
        }
        return true;
    }

    public static boolean isInclude(String fileName, Pattern includePattern) {
        if (includePattern == null) {
            return false;
        }
        return includePattern.matcher(fileName).find();
    }

    public static boolean isExclude(String fileName, Pattern excludePattern) {
        if (excludePattern == null) {
            return false;
        }
        return excludePattern.matcher(fileName).find();
    }


    public static class AppJar {
        public String name;
        public URL url;
        public long lastUpdated;

        public AppJar(String name, URL url, long lastUpdated) {
            this.name = name;
            this.url = url;
            this.lastUpdated = lastUpdated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppJar jar = (AppJar) o;
            return Objects.equals(name, jar.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, url, lastUpdated);
        }
    }

}
