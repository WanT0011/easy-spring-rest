package com.want.request.client;

import com.want.request.interceptor.EasySpringRestRequestInterceptor;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author want
 * @createTime 2021.01.11.22:25
 */
public class EasySpringRestClient {

    private List<EasySpringRestRequestInterceptor> interceptors;

    public Mono<ClientResponse> exchange(String uri, HttpMethod httpMethod,Boolean block) throws URISyntaxException {
        ClientRequest clientRequest = builderRequest(uri, httpMethod);

    }


    public ClientRequest builderRequest(String uri, HttpMethod httpMethod) throws URISyntaxException {
        return ClientRequest.method(httpMethod,new URI(uri)).build();
    }
}
