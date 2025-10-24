package com.renlijia.bootapp.example.biz;

import com.renlijia.athena.api.UserQueryService;
import com.renlijia.athena.api.request.GetByUserIdRequest;
import com.renlijia.athena.api.response.ApiResult;
import com.renlijia.athena.api.response.UserVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExampleBiz {


    @Resource
    private UserQueryService userQueryService;

    public String test(){
        GetByUserIdRequest getByUserIdRequest = new GetByUserIdRequest();
        getByUserIdRequest.setCorpId("xxx");
        getByUserIdRequest.setUserId("yyy");
        ApiResult<UserVO> byUserId = userQueryService.getByUserId(getByUserIdRequest);
        System.out.println("query athena:" + byUserId);
        return "x";
    }
}
