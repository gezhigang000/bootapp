package com.renlijia.bootapp.core.admin.controller;


import com.renlijia.bootapp.core.jetty.JettyAdminServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class AppInstallController {


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "请选择一个文件进行上传。";
        }
        try {
            String uploadDir = "/path/to/upload/directory/";
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(uploadedFile);
            return "文件上传成功！";
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败！";
        }
    }

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
