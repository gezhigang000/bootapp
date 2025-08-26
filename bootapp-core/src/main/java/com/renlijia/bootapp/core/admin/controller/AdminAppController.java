package com.renlijia.bootapp.core.admin.controller;

import com.renlijia.bootapp.core.admin.AppJarHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class AdminAppController {

    @RequestMapping(value = "/app")
    public String app(Model model) {
        List<AppJarHolder.AppJar> appJarList = AppJarHolder.getAppJarList();
        model.addAttribute("applist",appJarList);
        return "app";
    }


}
