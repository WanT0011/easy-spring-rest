package com.want.request.interceptor;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * request的请求执行器
 *
 * @author want
 * @createTime 2021.01.11.21:44
 */
@FunctionalInterface
public interface EasySpringRestRequestExecutor {

    /**
     * 执行请求，返回{@link WebClient.ResponseSpec}
     * @param request 请求
     * @return 响应
     */
    WebClient.ResponseSpec exec(ClientRequest request);

    /**
     * 将拦截器放在本执行器前，组合成一个新的执行器
     * @param filter
     * @return
     */
    default EasySpringRestRequestExecutor filter(EasySpringRestSendRequestExecutorFilter filter){
        Assert.notNull(filter, "'filter' must not be null");
        return filter.apply(this);
    }
}
