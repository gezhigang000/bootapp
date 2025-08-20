package com.renlijia.bootapp.core;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BootAppScan {

    @AliasFor("appName")
    String value() default "";

    @AliasFor("value")
    String appName() default "";

    String appVersion() default "";

    String appWebContext() default "";

    String[] basePackages() default {};
}
