package com.want.request.client;

import com.want.request.interceptor.EasySpringRestRequestExecutor;
import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;
import lombok.NonNull;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 目前所有的请求都是通过此客户端发出
 *
 * @author want
 * @createTime 2021.01.11.22:25
 */
public class EasySpringRestClient {

    /**
     * 内部封装的webClient，负责发送请求
     */
    private final WebClient webClient;

    /**
     * 发起请求的执行器
     */
    private EasySpringRestRequestExecutor easySpringRestRequestExecutor;

    public EasySpringRestClient(WebClient webClient, EasySpringRestRequestExecutor easySpringRestRequestExecutor) {
        this.webClient = webClient;
        this.easySpringRestRequestExecutor = easySpringRestRequestExecutor;
    }

    /**
     * 向指定的uri发送post请求
     * @param uri 目标的uri
     * @param body 请求实体
     * @param clazz 请求实体的class
     * @param <T> 请求实体的泛型
     * @return
     */
    public <T> WebClient.ResponseSpec post(String uri
            , T body, Class<T> clazz) {
        return exchange(uri,HttpMethod.POST,null,null
                ,BodyInserters.fromPublisher(Mono.just(body), clazz),null);
    }

    /**
     * rest请求执行方法
     * @param uri 目标的uri
     * @param httpMethod 请求方式
     * @param attConsumer 请求中传递的中间参数，可以理解为threadLocal的意思
     * @param cookieConsumer cookie的消费者，为请求增加cookie
     * @param body 请求实体
     * @param headersConsumer header的消费者，为请求增加header
     * @return
     */
    @SneakyThrows
    public WebClient.ResponseSpec exchange(String uri, HttpMethod httpMethod
            , Consumer<Map<String, Object>> attConsumer
            , Consumer<MultiValueMap<String, String>> cookieConsumer
            , BodyInserter<?, ? super ClientHttpRequest> body
            , Consumer<HttpHeaders> headersConsumer) {

        // 创建请求builder，TODO 此处需要替换成自己的request
        ClientRequest.Builder clientBuilder = ClientRequest.create(httpMethod, new URI(uri));
        Optional.ofNullable(attConsumer).ifPresent(clientBuilder::attributes);
        Optional.ofNullable(cookieConsumer).ifPresent(clientBuilder::cookies);
        Optional.ofNullable(body).ifPresent(clientBuilder::body);
        Optional.ofNullable(headersConsumer).ifPresent(clientBuilder::headers);
        // 构建请求
        ClientRequest req = clientBuilder.build();
        //执行请求
        return easySpringRestRequestExecutor.exec(req);
    }

    /**
     * 为执行器增加一个最前方的拦截器
     * TODO 后续可以对执行器和拦截器进行升级
     * @param filter 拦截器
     */
    public void addFilterFirst(@NonNull EasySpringRestSendRequestExecutorFilter filter){
        this.easySpringRestRequestExecutor = filter.apply(this.easySpringRestRequestExecutor);
    }

    /**
     * easySpringClient的客户端构造类
     */
    public static class Builder{
        /**
         * 发起请求的WebClient客户端
         */
        private WebClient webClient;
        /**
         * 拦截器的列表
         */
        private List<EasySpringRestSendRequestExecutorFilter> filterList;

        public Builder() {
            this.filterList = new ArrayList<>();
        }

        /**
         * 为拦截器列表增加一个拦截器
         * @param filter 拦截器
         * @return
         */
        public Builder filter(@NonNull EasySpringRestSendRequestExecutorFilter filter){
            filterList.add(filter);
            return this;
        }

        /**
         * 使用自定义的webClient进行发送请求
         * @param webClient
         * @return
         */
        public Builder webClient(WebClient webClient){
            this.webClient = webClient;
            return this;
        }

        /**
         * 允许自定义的操作拦截器
         * @param consumer 拦截器列表的消费者
         * @return
         */
        public Builder filters(Consumer<List<EasySpringRestSendRequestExecutorFilter>> consumer){
            consumer.accept(filterList);
            return this;
        }

        /**
         * 构造 EasySpringRestClient
         * @return EasySpringRestClient
         */
        public EasySpringRestClient build(){
            // 构造 初始的EasySpringRestClient 请求的执行器
            EasySpringRestRequestExecutor restRequestExecutor = initEasySpringRestRequestExecutor();
            // 将拦截器作用于执行器上，组合成新的执行其
            EasySpringRestRequestExecutor finalExecutor = filterList.stream()
                    .reduce(EasySpringRestSendRequestExecutorFilter::andThen)
                    .map(filter -> filter.apply(restRequestExecutor))
                    .orElse(restRequestExecutor);
            // 返回构造的新请求客户端
            return new EasySpringRestClient(webClient,finalExecutor);
        }

        /**
         * 构造初始的请求执行器
         *
         * @return
         */
        public EasySpringRestRequestExecutor initEasySpringRestRequestExecutor(){
            // 如果有外部自定义的 客户端使用外部的，如果没有使用默认的
            // TODO 此处默认的是从2.0.0老版拉过来的，需要重新创建
            WebClient finalWebClient = Optional.ofNullable(this.webClient)
                    .orElseGet(() -> {
                        ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector();
                        return WebClient.builder()
                                .clientConnector(reactorClientHttpConnector)
                                .build();
                    });
            // 执行器实例创建
            // 具体不必多说
            return req ->
                    finalWebClient.method(req.method())
                        .uri(req.url())
                        .headers(httpHeaders ->
                            Optional.ofNullable(req.headers())
                                    .map(HttpHeaders::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> httpHeaders.addAll(entry.getKey(),entry.getValue()))))
                        .attributes(attMap ->
                                Optional.ofNullable(req.attributes())
                                    .map(Map::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> attMap.put(entry.getKey(),entry.getValue()))))
                        .body(req.body())
                        .cookies(cookies ->
                                Optional.ofNullable(req.cookies())
                                    .map(Map::entrySet)
                                    .ifPresent(entries -> entries.forEach(entry -> cookies.addAll(entry.getKey(),entry.getValue()))))
                        .retrieve();
        }
    }

}
