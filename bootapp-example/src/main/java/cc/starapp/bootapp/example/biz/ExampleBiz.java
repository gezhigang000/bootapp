package cc.starapp.bootapp.example.biz;

import org.springframework.stereotype.Component;

@Component
public class ExampleBiz {



    public String test(){
        System.out.println("query athena:" );
        return "x";
    }
}
