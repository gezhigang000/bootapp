package com.renlijia.bootapp.example.controller;

import com.renlijia.bootapp.example.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @Autowired
    private ExampleService exampleService;

    @RequestMapping("/test")
    public String test(){
        return "vvv 1122 this is exam test" + exampleService.some("do exec");
    }

    @RequestMapping("/")
    public String home(){
        return " this is exam home";
    }
}
