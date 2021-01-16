package com.want.spring.cloud.consumer.client;

import com.want.request.client.EasySpringRestClient;
import com.want.spring.cloud.api.domain.req.SimplePostReqDTO;
import com.want.spring.cloud.api.domain.resp.SimpleRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author want
 * @createTime 2021.01.16.18:34
 */
@RestController
@RequestMapping("demo")
public class EasySpringRestClientDemoClient {

    @Resource
    private EasySpringRestClient demoSpringRestClient;

    @GetMapping("testPost")
    public SimpleRespDTO testPost(){
        SimplePostReqDTO reqDTO = SimplePostReqDTO.builder().curTime(LocalDateTime.now()).build();

        return demoSpringRestClient.post("http://cloud-provider/simple/just-post",reqDTO,SimplePostReqDTO.class)
                .bodyToMono(SimpleRespDTO.class)
                .block();
    }

}
