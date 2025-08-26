package com.renlijia.bootapp.example;

import com.renlijia.bootapp.core.HowInstall;

public class ExampleHowInstall implements HowInstall {
    @Override
    public String appName() {
        return "exam";
    }

    @Override
    public String appWebContext() {
        return null;
    }

    @Override
    public String[] basePackages() {
        return new String[]{"com.renlijia.bootapp.example"};
    }

    @Override
    public Class appClass() {
        return AppConfig.class;
    }

    @Override
    public String appJarAbsolutePath() {
        return "/home/";
    }

    @Override
    public String appJarIncludeByNameRegex() {
        return ".*example.*";
    }

    @Override
    public String appJareExcludeByNameRegex() {
        return "";
    }

}
