package com.want.request.interceptor.filter;

import com.want.exception.ReqUriIsNullException;
import com.want.request.interceptor.EasySpringRestRequestExecutor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;

/**
 * 抽象的负载均衡拦截器，将统一的请求替换成新请求
 *
 * @author WangZhiJian
 * @since 2021/1/14
 */
public abstract class AbstractLoadBalanceExecutorFilter implements LoadBalanceExecutorFilter {


    /**
     * 在拦截操作中，使用负载均衡选择服务的uri
     * @param request
     * @param next
     * @return
     */
    @Override
    public WebClient.ResponseSpec filter(ClientRequest request, EasySpringRestRequestExecutor next) {
        // 构建新的uri
        URI newUri = Optional.ofNullable(request.url())
                .map(this::doSelect)
                .orElseThrow(() -> new ReqUriIsNullException(request));
        // 构建新的请求
        ClientRequest newRequest = ClientRequest.method(request.method(), newUri)
                .headers(headers -> headers.addAll(request.headers()))
                .cookies(cookies -> cookies.addAll(request.cookies()))
                .attributes(attributes -> attributes.putAll(request.attributes()))
                .body(request.body()).build();
        // 交给后续的执行器做执行
        return next.exec(newRequest);
    }

}
