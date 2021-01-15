package com.want.request.interceptor;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * TODO  改成自己的 request，更容易的更改header之类的
 *
 * @author want
 * @createTime 2021.01.11.21:41
 */
@FunctionalInterface
public interface EasySpringRestSendRequestExecutorFilter {

    WebClient.ResponseSpec filter(ClientRequest request, EasySpringRestRequestExecutor next);

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
