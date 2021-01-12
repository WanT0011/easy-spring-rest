package com.want.request.interceptor;

import jdk.nashorn.internal.objects.annotations.Function;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * @author want
 * @createTime 2021.01.11.21:44
 */
@FunctionalInterface
public interface EasySpringRestRequestExecutor {

    Mono<ClientResponse> exec(ClientRequest request);

    default EasySpringRestRequestExecutor filter(EasySpringRestSendRequestExecutorFilter filter){
        Assert.notNull(filter, "'filter' must not be null");
        return filter.apply(this);
    }
}
