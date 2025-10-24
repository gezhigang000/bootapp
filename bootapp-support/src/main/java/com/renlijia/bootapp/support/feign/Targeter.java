package com.renlijia.bootapp.support.feign;

import feign.Feign;
import feign.Target;


public interface Targeter {
    <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, FeignContext context,
                 Target.HardCodedTarget<T> target);
}