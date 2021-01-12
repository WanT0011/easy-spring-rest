package com.want.request.interceptor;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * @author want
 * @createTime 2021.01.11.21:41
 */
@FunctionalInterface
public interface EasySpringRestSendRequestExecutorFilter {

    Mono<ClientResponse> filter(ClientRequest request, EasySpringRestRequestExecutor next);

    default EasySpringRestSendRequestExecutorFilter andThen(EasySpringRestSendRequestExecutorFilter after){
        Assert.notNull(after, "'after filter' must not be null");
        return (req,next) ->{
            EasySpringRestRequestExecutor nextExec = request -> after.filter(request,next);
            return this.filter(req,nextExec);
        };
    }

    default EasySpringRestRequestExecutor apply(EasySpringRestRequestExecutor executor){
        Assert.notNull(executor, "'executor' must not be null");
        return req -> this.filter(req,executor);
    }
}
