package com.want.request.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author want
 * @createTime 2021.01.11.22:50
 */
@AllArgsConstructor
public class DefaultEasySpringRestRequestInterceptor implements EasySpringRestSendRequestInterceptor {

    private final int order;

    @Override
    public Mono<ClientResponse> intercept(ClientRequest request, EasySpringRestRequestInterceptor next) {
        return Optional.ofNullable(next)
                .map(next.intercept(request,));
    }

    @Override
    public int order() {
        return order;
    }
}
