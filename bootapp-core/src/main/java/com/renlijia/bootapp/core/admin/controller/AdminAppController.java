package com.renlijia.bootapp.core.admin.controller;

import com.renlijia.bootapp.core.app.AppJarHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AdminAppController {

    @RequestMapping(value = "/app")
    public String app(Model model) {
        List<AppJarHolder.AppJar> appJarList = AppJarHolder.instance().allAppJarForRead();
        model.addAttribute("applist",appJarList);
        return "app";
    }


}
