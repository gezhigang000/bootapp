package cc.starapp.bootapp.core;

public interface HowInstall {

    String appName() ;

    String appWebContext() ;

    String[] basePackages();

    Class appClass();

}
