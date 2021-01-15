package com.want.request.client;

import com.want.request.interceptor.EasySpringRestRequestExecutor;
import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author want
 * @createTime 2021.01.11.22:25
 */
public class EasySpringRestClient {

    private final WebClient webClient;

    private EasySpringRestRequestExecutor easySpringRestRequestExecutor;

    public EasySpringRestClient(WebClient webClient, EasySpringRestRequestExecutor easySpringRestRequestExecutor) {
        this.webClient = webClient;
        this.easySpringRestRequestExecutor = easySpringRestRequestExecutor;
    }

    public <T> WebClient.ResponseSpec post(String uri
            , T body, Class<T> clazz) {
        return exchange(uri,HttpMethod.POST,null,null
                ,BodyInserters.fromPublisher(Mono.just(body), clazz),null);
    }

    @SneakyThrows
    public WebClient.ResponseSpec exchange(String uri, HttpMethod httpMethod
            , Consumer<Map<String, Object>> attConsumer
            , Consumer<MultiValueMap<String, String>> cookieConsumer
            , BodyInserter<?, ? super ClientHttpRequest> body
            , Consumer<HttpHeaders> headersConsumer) {

        ClientRequest.Builder clientBuilder = ClientRequest.method(httpMethod, new URI(uri));
        Optional.ofNullable(attConsumer).ifPresent(clientBuilder::attributes);
        Optional.ofNullable(cookieConsumer).ifPresent(clientBuilder::cookies);
        Optional.ofNullable(body).ifPresent(clientBuilder::body);
        Optional.ofNullable(headersConsumer).ifPresent(clientBuilder::headers);

        ClientRequest req = clientBuilder.build();

        return easySpringRestRequestExecutor.exec(req);
    }

    public void addFilterFirst(EasySpringRestSendRequestExecutorFilter filter){
        this.easySpringRestRequestExecutor = filter.apply(this.easySpringRestRequestExecutor);
    }



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
            WebClient finalWebClient = Optional.ofNullable(this.webClient)
                    .orElseGet(() -> {
                        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector();
                        return WebClient.builder()
                                .clientConnector(reactorClientHttpConnector)
                                .build();
                    });
            return req ->
                    finalWebClient.method(req.method())
                        .uri(req.url())
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
                        }).retrieve();
        }
    }

}
