package com.renlijia.bootapp.example.controller;

import com.renlijia.bootapp.example.biz.ExampleBiz;
import com.renlijia.bootapp.example.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @Value("${example.p}")
    private String p;
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private ExampleBiz exampleBiz;

    @RequestMapping("/test")
    public String test(){
        return "xxx test" + exampleService.some("do exec");
    }

    @RequestMapping("/test/biz")
    public String home(){

        System.out.println("test.a:" + p);
        String test = exampleBiz.test();
        return " this is exam home vvv hh jj lll ;llkk lkmmm ppp ooo";
    }
}
