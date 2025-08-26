package com.renlijia.bootapp.core.admin.controller;


import com.renlijia.bootapp.core.jetty.JettyAdminServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppInstallController {



    @RequestMapping(value = "reInstall")
    public String reInstall() {
        try {
            JettyAdminServer.reInstall();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "success";
    }
}
