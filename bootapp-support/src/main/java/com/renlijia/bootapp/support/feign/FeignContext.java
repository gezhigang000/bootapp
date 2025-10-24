package com.renlijia.bootapp.support.feign;


public class FeignContext extends NamedContextFactory<FeignClientSpecification> {

    public FeignContext(Class<?> configClass) {
        super(configClass, "feign", "feign.client.name");
    }

}