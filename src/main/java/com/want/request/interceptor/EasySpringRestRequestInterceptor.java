package com.want.request.interceptor;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * @author want
 * @createTime 2021.01.11.21:44
 */
public interface EasySpringRestRequestInterceptor {

    Mono<ClientResponse> intercept(ClientRequest request,Iterable<EasySpringRestRequestInterceptor> next);

    int order();
}
