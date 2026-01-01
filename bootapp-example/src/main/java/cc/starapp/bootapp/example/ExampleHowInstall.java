package cc.starapp.bootapp.example;

import cc.starapp.bootapp.core.HowInstall;

public class ExampleHowInstall implements HowInstall {
    @Override
    public String appName() {
        return "exam";
    }

    @Override
    public String appWebContext() {
        return "exam";
    }

    @Override
    public String[] basePackages() {
        return new String[]{"cc.starapp.bootapp.example"};
    }

    @Override
    public Class appClass() {
        return AppConfig.class;
    }



}
