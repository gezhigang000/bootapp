package com.renlijia.bootapp.example.api;


import com.renlijia.bootapp.support.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient
public interface ExampleFeignApi {

    @RequestMapping(method = RequestMethod.GET, value = "/feign/test")
    String feignTest();
}
