package com.renlijia.bootapp.core.admin.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppInstallController {

    @RequestMapping(value = "reInstall")
    public String reInstall() {
        return "success";
    }
}
