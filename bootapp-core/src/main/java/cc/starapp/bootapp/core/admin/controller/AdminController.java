package cc.starapp.bootapp.core.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {


    @RequestMapping("/hi")
    public String hi(){
        return "hi this is admin console";
    }


}
