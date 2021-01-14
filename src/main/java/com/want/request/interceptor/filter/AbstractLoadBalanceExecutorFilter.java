package com.want.request.interceptor.filter;

import com.want.exception.ReqUriIsNullException;
import com.want.request.interceptor.EasySpringRestRequestExecutor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;

/**
 * @author WangZhiJian
 * @since 2021/1/14
 */
public abstract class AbstractLoadBalanceExecutorFilter implements LoadBalanceExecutorFilter {


    @Override
    public WebClient.ResponseSpec filter(ClientRequest request, EasySpringRestRequestExecutor next) {

        URI newUri = Optional.ofNullable(request.url())
                .map(this::doSelect)
                .orElseThrow(() -> new ReqUriIsNullException(request));

        ClientRequest newRequest = ClientRequest.method(request.method(), newUri)
                .headers(headers -> headers.addAll(request.headers()))
                .cookies(cookies -> cookies.addAll(request.cookies()))
                .attributes(attributes -> attributes.putAll(request.attributes()))
                .body(request.body()).build();

        return next.exec(newRequest);
    }

}
