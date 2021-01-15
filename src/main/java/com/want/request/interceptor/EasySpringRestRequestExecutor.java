package com.want.request.interceptor;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author want
 * @createTime 2021.01.11.21:44
 */
@FunctionalInterface
public interface EasySpringRestRequestExecutor {

    WebClient.ResponseSpec exec(ClientRequest request);

    default EasySpringRestRequestExecutor filter(EasySpringRestSendRequestExecutorFilter filter){
        Assert.notNull(filter, "'filter' must not be null");
        return filter.apply(this);
    }
}
