package com.renlijia.bootapp.core.admin.controller;


import com.renlijia.bootapp.core.BootAppLoader;
import com.renlijia.bootapp.core.admin.RootDeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static io.undertow.servlet.Servlets.defaultContainer;

@RestController
public class DeployController {



    @RequestMapping(value = "deploy")
    public String deploy(@RequestParam("jar") String appJarPath, @RequestParam(value = "dep",required = false) String dependenceJarPath) {


        BootAppLoader.registerServlet(appJarPath,dependenceJarPath);

        return "success";

    }
}
