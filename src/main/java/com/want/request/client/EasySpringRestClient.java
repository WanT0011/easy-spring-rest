package com.want.request.client;

import com.want.request.interceptor.EasySpringRestRequestExecutor;
import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author want
 * @createTime 2021.01.11.22:25
 */
public class EasySpringRestClient {

    private WebClient webClient;

    private EasySpringRestRequestExecutor easySpringRestRequestExecutor;

    public EasySpringRestClient(EasySpringRestRequestExecutor easySpringRestRequestExecutor) {
        this.easySpringRestRequestExecutor = easySpringRestRequestExecutor;
    }

    public EasySpringRestClient(WebClient webClient, EasySpringRestRequestExecutor easySpringRestRequestExecutor) {
        this.webClient = webClient;
        this.easySpringRestRequestExecutor = easySpringRestRequestExecutor;
    }

    public Mono<ClientResponse> exchange(String uri, HttpMethod httpMethod
            , Consumer<Map<String, Object>> attConsumer
            , Consumer<MultiValueMap<String, String>> cookieConsumer
            , BodyInserter<?, ? super ClientHttpRequest> body
            ,Consumer<HttpHeaders> headersConsumer
            , Boolean block) throws URISyntaxException {
        ClientRequest req = ClientRequest.method(httpMethod, new URI(uri))
                .attributes(attConsumer)
                .cookies(cookieConsumer)
                .headers(headersConsumer)
                .body(body)
                .build();

        return easySpringRestRequestExecutor.exec(req);
    }


//    public void addInterceptorLast(EasySpringRestRequestExecutor easySpringRestRequestInterceptor){
//        interceptors.add(easySpringRestRequestInterceptor);
//    }
//    public void addInterceptor(int index, EasySpringRestRequestExecutor easySpringRestRequestInterceptor){
//        Assert.isTrue(index < interceptors.size(),"设置拦截器的下标越界！");
//        interceptors.add(index,easySpringRestRequestInterceptor);
//    }


    public ClientRequest builderRequest(String uri, HttpMethod httpMethod) throws URISyntaxException {
        return ClientRequest.method(httpMethod,new URI(uri)).build();
    }

    public static class Builder{

        private WebClient webClient;

        private List<EasySpringRestSendRequestExecutorFilter> filterList;

        public Builder() {
            this.filterList = new ArrayList<>();
        }

        public Builder filter(EasySpringRestSendRequestExecutorFilter filter){
            filterList.add(filter);
            return this;
        }
        public Builder webClient(WebClient webClient){
            this.webClient = webClient;
            return this;
        }
        public Builder filters(Consumer<List<EasySpringRestSendRequestExecutorFilter>> consumer){
            consumer.accept(filterList);
            return this;
        }

        public EasySpringRestClient build(){
            EasySpringRestRequestExecutor restRequestExecutor = initEasySpringRestRequestExecutor();
            EasySpringRestRequestExecutor finalExecutor = filterList.stream()
                    .reduce(EasySpringRestSendRequestExecutorFilter::andThen)
                    .map(filter -> filter.apply(restRequestExecutor))
                    .orElse(restRequestExecutor);
            return new EasySpringRestClient(webClient,finalExecutor);
        }


        public EasySpringRestRequestExecutor initEasySpringRestRequestExecutor(){
            WebClient webClient = Optional.ofNullable(this.webClient)
                    .orElseGet(() -> {
                        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector();
                        return WebClient.builder()
                                .clientConnector(reactorClientHttpConnector)
                                .build();
                    });
            return req ->
                webClient.method(req.method())
                        .headers(httpHeaders -> {
                            Optional.ofNullable(req.headers())
                                    .map(HttpHeaders::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> httpHeaders.addAll(entry.getKey(),entry.getValue())));
                        })
                        .attributes(attMap -> {
                            Optional.ofNullable(req.attributes())
                                    .map(Map::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> attMap.put(entry.getKey(),entry.getValue())));
                        })
                        .body(req.body())
                        .cookies(cookies -> {
                            Optional.ofNullable(req.cookies())
                                    .map(Map::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> cookies.addAll(entry.getKey(),entry.getValue())));
                        }).exchange();
        }
    }

}
