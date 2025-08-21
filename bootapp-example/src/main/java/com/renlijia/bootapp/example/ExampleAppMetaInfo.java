package com.renlijia.bootapp.example;

import com.renlijia.bootapp.core.AppMetaInfo;

public class ExampleAppMetaInfo implements AppMetaInfo {
    @Override
    public String appName() {
        return "exam";
    }

    @Override
    public String appVersion() {
        return "1";
    }

    @Override
    public String appWebContext() {
        return "exam";
    }

    @Override
    public String[] basePackages() {
        return new String[]{"com.renlijia.bootapp.example"};
    }

    @Override
    public Class appClass() {
        return AppConfig.class;
    }
}
