package com.renlijia.bootapp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BootAppClassloader  extends URLClassLoader {

    private Logger log = LoggerFactory.getLogger(BootAppClassloader.class) ;

    public static final String[] DEFAULT_EXCLUDED_PACKAGES = new String[]{"java.", "javax.", "sun.", "oracle."};

    private final Set<String> excludedPackages = new HashSet(Arrays.asList(DEFAULT_EXCLUDED_PACKAGES.clone()));

    private final Set<String> overridePackages = new HashSet<>();

    public BootAppClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> result = null;
        synchronized (BootAppClassloader.class) {
            if (isOverriding(name)) {
                if (log.isInfoEnabled()) {
                    log.info("Load class for overriding: {}", name);
                }
                result = loadClassForOverriding(name);
            }
            if (Objects.nonNull(result)) {
                // 链接类
                if (resolve) {
                    resolveClass(result);
                }
                return result;
            }
        }
        // 使用默认类加载方式
        return super.loadClass(name, resolve);
    }

    private Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
        // 查找已加载的类
        Class<?> result = findLoadedClass(name);
        if (Objects.isNull(result)) {
            // 加载类
            result = findClass(name);
        }
        return result;
    }

    private boolean isOverriding(final String name) {
        if(name == null){
            return false;
        }
        return !isExcluded(name) && isInclude(name);
    }

    protected boolean isInclude(String className) {
        if(className == null){
            return false;
        }
        for (String packageName : this.overridePackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isExcluded(String className) {
        if(className == null){
            return false;
        }
        for (String packageName : this.excludedPackages) {
            if (className.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void addExcludedPackages(Set<String> excludedPackages) {
        this.excludedPackages.addAll(excludedPackages);
    }

    public void addOverridePackages(Set<String> overridePackages) {
        this.overridePackages.addAll(overridePackages);
    }

}
