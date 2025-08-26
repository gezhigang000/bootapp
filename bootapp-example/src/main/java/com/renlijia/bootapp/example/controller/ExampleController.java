package com.renlijia.bootapp.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @RequestMapping("/test")
    public String test(){
        return "vvv 1122 this is exam test";
    }

    @RequestMapping("/")
    public String home(){
        return " this is exam home";
    }
}
