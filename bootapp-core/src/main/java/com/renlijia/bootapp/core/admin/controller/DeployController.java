package com.renlijia.bootapp.core.admin.controller;


import com.renlijia.bootapp.core.AppLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {



    @RequestMapping(value = "deploy")
    public String deploy(@RequestParam("jar") String appJarPath, @RequestParam(value = "dep",required = false) String dependenceJarPath) {
        AppLoader.registerServlet(appJarPath,dependenceJarPath);
        return "success";

    }
}
