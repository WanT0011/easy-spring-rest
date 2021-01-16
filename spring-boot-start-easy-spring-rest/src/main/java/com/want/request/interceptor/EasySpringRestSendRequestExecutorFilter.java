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
    /**
     * 对请求进行拦截处理，之后交由后续的执行器做执行
     * @param request
     * @param next
     * @return
     */
    WebClient.ResponseSpec filter(ClientRequest request, EasySpringRestRequestExecutor next);

    /**
     * 在本拦截器后插入一个拦截器
     * @param after 要插入的拦截器
     * @return
     */
    default EasySpringRestSendRequestExecutorFilter andThen(EasySpringRestSendRequestExecutorFilter after){
        Assert.notNull(after, "'after filter' must not be null");
        return (req,next) ->{
            EasySpringRestRequestExecutor nextExec = request -> after.filter(request,next);
            return this.filter(req,nextExec);
        };
    }

    /**
     * 将本拦截器放在执行器前方，并合成一个执行器
     * @param executor
     * @return
     */
    default EasySpringRestRequestExecutor apply(EasySpringRestRequestExecutor executor){
        Assert.notNull(executor, "'executor' must not be null");
        return req -> this.filter(req,executor);
    }
}
