package com.renlijia.bootapp.core.app;

import com.renlijia.bootapp.core.EmbeddedAppConfig;
import com.renlijia.bootapp.core.HowInstall;
import com.renlijia.bootapp.core.boot.BootContext;
import com.renlijia.bootapp.core.boot.jarfile.JarFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppJarHolder {

    private static final Logger logger = LoggerFactory.getLogger(AppJarHolder.class);

    private static final AppJarHolder _this = new AppJarHolder();

    public static Pattern JAR_NAME_PATTERN = Pattern.compile("[\\d+.]+");
    private static volatile boolean init = false;

    public static AppJarHolder instance() {
        return _this;
    }

    public static synchronized void init(EmbeddedAppConfig embeddedAppConfig) {
        if (init) {
            throw new RuntimeException("AppJarHolder has init....");
        }
        _this.setEmbeddedAppConfig(embeddedAppConfig);
        init = true;
    }

    private Set<AppJar> appJarSet = new TreeSet<>();

    private HowInstall howInstall;

    private EmbeddedAppConfig embeddedAppConfig;

    private AppClassloader appClassloader;


    public void setAppClassloader(AppClassloader appClassloader) {
        this.appClassloader = appClassloader;
    }

    public boolean remove(AppJar o) {
        return appJarSet.remove(o);
    }

    public boolean add(AppJar o) {
        return appJarSet.add(o);
    }

    public List<AppJar> allAppJarForRead() {
        return appJarSet.stream().sorted(Comparator.comparing(o -> o.name)).collect(Collectors.toList());
    }

    public void setEmbeddedAppConfig(EmbeddedAppConfig embeddedAppConfig) {
        this.embeddedAppConfig = embeddedAppConfig;
    }

    private void clean() {
        appJarSet = new HashSet<>();
        howInstall = null;
    }


    public boolean reload() throws Exception {
        Set<AppJar> newJarSet = new HashSet<>();
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
                continue;
            }
            if (!file.canRead()) {
                continue;
            }
            File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));
            if (files == null || files.length == 0) {
                continue;
            }
            for (File f : files) {
                String name = f.getName();
                if (match(name, includePattern, excludePattern)) {
                    Calendar instance = Calendar.getInstance();
                    instance.setTimeInMillis(f.lastModified());
                    AppJar appJar = new AppJar(name, f.toURI().toURL(), instance.getTime(), jarDir);
                    String checksum = JarFileUtils.checksum(f);
                    appJar.setChecksum(checksum);
                    newJarSet.add(appJar);
                }
            }
        }

        if (newJarSet.size() == 0) {
            logger.info("jar file is empty ! ignore reload app");
            return false;
        }
        Set<AppJar> andFilterSameJar = createAndFilterSameJar(newJarSet);
        if (andFilterSameJar.equals(appJarSet)) {
            logger.info("jar file no change ignore reload app! , new jar:{},old jar:{}", newJarSet, appJarSet);
            return false;
        }
        logger.info("jar file changed start reload app! , new jar:{},old jar:{}", newJarSet, appJarSet);
        clean();
        appJarSet.addAll(andFilterSameJar);
        loadHowInstall();
        return true;
    }

    private Set<AppJar> createAndFilterSameJar(Set<AppJar> newJarSet) {
        List<AppJar> appJarList = new ArrayList<>(newJarSet);
        //找到最新的jar
        appJarList.sort((o1, o2) -> o2.getLastUpdated().compareTo(o1.getLastUpdated()));
        Iterator<AppJar> iterator = appJarList.iterator();
        List<String> jarNameList = new ArrayList<>();
        while (iterator.hasNext()) {
            AppJar next = iterator.next();
            String jarName = findJarName(next.getName());
            if (jarName == null) {
                logger.warn("match jar name null, jar name:{},url;{}", next.getName(), next.getUrl());
                continue;
            }
            if (jarNameList.contains(jarName)) {
                iterator.remove();
            }
        }
        Set<AppJar> newnewAppJar = new HashSet<>();
        newnewAppJar.addAll(appJarList);
        return newnewAppJar;
    }

    private String findJarName(String jarFullName) {
        Matcher matcher = JAR_NAME_PATTERN.matcher(jarFullName);
        if (!matcher.find()) {
            logger.error("findJarName pattern matcher error, jar:{}", jarFullName);
            return null;
        }
        String group = matcher.group();
        return jarFullName.split(group)[0];
    }

    private void loadHowInstall() {
        logger.info("load jar : {}" ,appJarSet);
        List<URL> urlList = appJarSet.stream().map(jar -> jar.url).collect(Collectors.toList());
        appClassloader = new AppClassloader(urlList.toArray(new URL[]{}), BootContext.instance().getClassLoader());
        Thread.currentThread().setContextClassLoader(appClassloader);
        List<HowInstall> howInstalls = new ArrayList<>();
        ServiceLoader.load(HowInstall.class, appClassloader).forEach(howInstalls::add);
        if (howInstalls.size() != 1) {
            throw new RuntimeException("HowInstall instances has and only has one");
        }
        Thread.currentThread().setContextClassLoader(appClassloader);
        howInstall = howInstalls.get(0);
    }

    public AppClassloader getAppClassloader() {
        return appClassloader;
    }

    public HowInstall getHowInstall() {
        return howInstall;
    }

    public boolean match(String fileName, Pattern includePattern, Pattern excludePattern) {
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

    public boolean isInclude(String fileName, Pattern includePattern) {
        if (includePattern == null) {
            return false;
        }
        return includePattern.matcher(fileName).find();
    }

    public boolean isExclude(String fileName, Pattern excludePattern) {
        if (excludePattern == null) {
            return false;
        }
        return excludePattern.matcher(fileName).find();
    }

    public EmbeddedAppConfig getEmbeddedAppConfig() {
        return embeddedAppConfig;
    }

    public static class AppJar {
        public String name;
        public URL url;
        public Date lastUpdated;
        private String loadDir;
        private String checksum;

        public AppJar(String name, URL url, Date lastUpdated, String loadDir) {
            this.name = name;
            this.url = url;
            this.lastUpdated = lastUpdated;
            this.loadDir = loadDir;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public String fullPath() {
            return loadDir + File.separator + name;
        }

        public String getLoadDir() {
            return loadDir;
        }

        public void setLoadDir(String loadDir) {
            this.loadDir = loadDir;
        }

        public String getName() {
            return name;
        }

        public URL getUrl() {
            return url;
        }


        public Date getLastUpdated() {
            return lastUpdated;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            AppJar appJar = (AppJar) o;
            return Objects.equals(getName(), appJar.getName()) && Objects.equals(getUrl(), appJar.getUrl()) && Objects.equals(getLastUpdated(), appJar.getLastUpdated()) && Objects.equals(getLoadDir(), appJar.getLoadDir()) && Objects.equals(getChecksum(), appJar.getChecksum());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getUrl(), getLastUpdated(), getLoadDir(), getChecksum());
        }

        @Override
        public String toString() {
            return "AppJar{" +
                    "name='" + name + '\'' +
                    ", url=" + url +
                    ", lastUpdated=" + lastUpdated +
                    ", loadDir='" + loadDir + '\'' +
                    ", checksum='" + checksum + '\'' +
                    '}';
        }
    }

}
