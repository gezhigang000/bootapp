package com.renlijia.bootapp.support.feign;

import feign.Logger;


public interface FeignLoggerFactory {
    public Logger create(Class<?> type);
}
