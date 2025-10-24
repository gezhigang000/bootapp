package com.renlijia.bootapp.core.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminHealthController {

    @GetMapping("/health")
    public String health() {
        return "ok";

    }
}
