package com.renlijia.bootapp.core;

public interface HowInstall {

    String appName() ;

    String appWebContext() ;

    String[] basePackages();

    Class appClass();

    String appJarIncludeByNameRegex();

    String appJareExcludeByNameRegex();
}
