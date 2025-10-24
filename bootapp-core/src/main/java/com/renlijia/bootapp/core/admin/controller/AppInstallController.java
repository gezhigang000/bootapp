package com.renlijia.bootapp.core.admin.controller;


import com.renlijia.bootapp.core.admin.controller.data.ApiResponse;
import com.renlijia.bootapp.core.admin.controller.data.JarData;
import com.renlijia.bootapp.core.app.AppJarHolder;
import com.renlijia.bootapp.core.boot.BootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AppInstallController {

    @Value("${spring.http.multipart.location}")
    private String tempFileDir;

    private Logger logger = LoggerFactory.getLogger(AppInstallController.class);

    @PostMapping("/jar/upload")
    public ApiResponse<JarData> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("oldJar") String oldJar) {
        if (file.isEmpty()) {
            return ApiResponse.paramError("文件不能为空");
        }
        if (oldJar == null || oldJar.isEmpty()) {
            return ApiResponse.paramError("jar名称不能空");
        }
        String oldPrefixName = findJarName(oldJar);
        String beReplacePrefixName = findJarName(file.getOriginalFilename());
        if(!oldPrefixName.equals(beReplacePrefixName)){
            return ApiResponse.paramError("无法替换，上传的jar[" + beReplacePrefixName + "]和当前行的jar[" + oldPrefixName+"]名称不一致");
        }

        AppJarHolder.AppJar oldInstalledJar = findStoreDir(oldJar);
        if (oldInstalledJar == null) {
            return ApiResponse.paramError("根据jar名称无法匹配到初始安装的jar");
        }
        String oldFileBackup = oldInstalledJar.getLoadDir() + File.separator + UUID.randomUUID();
        try {
            File oldFile = new File(oldInstalledJar.fullPath());
            if (oldFile.exists()) {
                oldFile.renameTo(new File(oldFileBackup));
            }

            File temp = new File(tempFileDir + File.separator + UUID.randomUUID());
            if (!temp.getParentFile().exists()) {
                temp.getParentFile().mkdirs();
            }

            file.transferTo(temp);
            File targetFile = new File(oldInstalledJar.getLoadDir() + File.separator + file.getOriginalFilename());
            FileCopyUtils.copy(temp, targetFile);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(targetFile.lastModified());
            AppJarHolder.AppJar newAppJar = new AppJarHolder.AppJar(file.getOriginalFilename(),
                    targetFile.toURI().toURL(), calendar.getTime(), oldInstalledJar.getLoadDir());
            JarData jarData = new JarData(newAppJar);
            AppJarHolder.instance().remove(oldInstalledJar);
            AppJarHolder.instance().add(newAppJar);
            File f =new File(oldFileBackup);
            if(f.exists()){
                f.delete();
            }
            return ApiResponse.success(jarData);
        } catch (Exception e) {
            logger.error("upload jar error", e);
            File f = new File(oldFileBackup);
            if(f.exists()){
                f.renameTo(new File(oldInstalledJar.fullPath()));
            }
            return ApiResponse.bizError("文件上传失败," + e.getMessage());
        }
    }

    private AppJarHolder.AppJar findStoreDir(String oldJar) {
        String oldPrefixName = findJarName(oldJar);
        List<AppJarHolder.AppJar> appJarList = AppJarHolder.instance().allAppJarForRead();
        for (AppJarHolder.AppJar appJar : appJarList) {
            String jarName = findJarName(appJar.name);
            if (jarName.equals(oldPrefixName)) {
                return appJar;
            }
        }
        return null;
    }

    private String findJarName(String jarFullName){
        Matcher matcher = AppJarHolder.JAR_NAME_PATTERN.matcher(jarFullName);
        if (!matcher.find()) {
            logger.error("findJarName pattern matcher error, jar:{}", jarFullName);
            return null;
        }
        String group = matcher.group();
        return jarFullName.split(group)[0];
    }


    @RequestMapping(value = "reInstall")
    public ApiResponse<String> reInstall() {
        try {
            Thread thread = new Thread(() -> {
                try {
                    BootContext.instance().reloadDynamicApp();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
            return ApiResponse.success("安装程序运行中，请根据日志判断应用是否成功加载。");
        } catch (Exception e) {
            logger.error("re install error", e);
            return ApiResponse.bizError("重新安装失败，请检查日志。");
        }

    }

    @RequestMapping(value = "reInstallFromOss")
    public ApiResponse<String> reInstallFromOss() {
        return ApiResponse.bizError("尚未实现。");
    }


}
