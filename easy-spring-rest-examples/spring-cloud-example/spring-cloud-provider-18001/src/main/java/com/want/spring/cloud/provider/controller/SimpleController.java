package com.want.spring.cloud.provider.controller;

import com.want.spring.cloud.api.domain.req.SimpleGetReqDTO;
import com.want.spring.cloud.api.domain.req.SimplePostReqDTO;
import com.want.spring.cloud.api.domain.resp.SimpleRespDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author want
 * @createTime 2021.01.16.14:09
 */
@RestController
@RequestMapping("simple")
public class SimpleController {

    @Value("server.port")
    private String curPort;

    @GetMapping("just-get")
    public SimpleRespDTO justGet(SimpleGetReqDTO reqDTO){
        return SimpleRespDTO.builder().curTime(reqDTO.getCurTime().plusYears(1)).localPort(curPort).build();
    }

    @PostMapping("just-post")
    public SimpleRespDTO justGet(@RequestBody SimplePostReqDTO reqDTO){
        return SimpleRespDTO.builder().curTime(reqDTO.getCurTime().plusYears(1)).localPort(curPort).build();
    }
}
