//package com.want.factory;
//
//import com.want.config.WebClientConfig;
//import com.want.config.WebClientCustomProperties;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.SmartInitializingSingleton;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//
//import org.springframework.util.StringUtils;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.ipc.netty.http.client.HttpClient;
//
//
//import javax.annotation.Resource;
//import java.time.Duration;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//import static com.want.constant.EasySpringRestClientConstant.DEFAULT_CLIENT_NAME;
//import static java.time.temporal.ChronoUnit.SECONDS;
//import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
//
///**
// * @author want
// * @createTime 2021.01.08.23:39
// */
//@Slf4j
//@Configuration
//public class EasyWebClientFactory implements SmartInitializingSingleton, ApplicationContextAware {
//    @Resource
//    private WebClientConfig webClientConfig;
//
//    private ApplicationContext applicationContext;
//
//    private Map<String, ApplicationContext> clientCacheMap;
//
//    /**
//     * 初始化并构建客户端map
//     */
//    @Override
//    public void afterSingletonsInstantiated() {
//        log.info("创建web请求客户端缓存开始!");
//        long start = System.currentTimeMillis();
//
//        Optional.ofNullable(webClientConfig.getClientConfig())
//                .map(configMap -> {configMap.put(DEFAULT_CLIENT_NAME,new WebClientCustomProperties());return configMap;})
//                .map(Map::entrySet)
//                .ifPresent(
//                        entries ->{
//                            clientCacheMap = new ConcurrentHashMap<>(entries.size() * 4 / 3 + 1 );
//                            entries.stream()
//                                    .filter(Objects::nonNull)
//                                    .forEach(entry -> {
//                                        log.info("构建【{}】webClient开始",entry.getKey());
//                                        WebClientCustomProperties properties = entry.getValue();
//                                        String baseUrl = Optional.ofNullable(properties.getBaseUrl()).orElse(properties.getDefaultUriVariables());
//                                        ReactorClientHttpConnector reactorClientHttpConnector;
//                                        if(!properties.getUseGlobalResources()){
//                                            reactorClientHttpConnector = Optional.ofNullable(properties.getReactorResourceFactoryBeanName())
//                                                    .map(beanName -> applicationContext.getBean(beanName, ReactorClientHttpConnector.class))
//                                                    .orElse(null);
//                                        }else {
//                                            reactorClientHttpConnector = new ReactorClientHttpConnector();
//                                        }
//                                        WebClient.Builder builder = WebClient.builder()
//                                                .defaultCookies(cookieList ->
//                                                        Optional.ofNullable(properties.getCookies())
//                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> cookieList.add(cookie.getKey(), cookie.getValue()))))
//                                                .defaultHeaders(headList ->
//                                                        Optional.ofNullable(properties.getHeaders())
//                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> headList.add(cookie.getKey(), cookie.getValue()))))
//                                                .filters(filterList -> Optional.ofNullable(properties.getFilterNames())
//                                                        .ifPresent(filterNames -> filterNames.stream().map(name -> applicationContext.getBean(name, ExchangeFilterFunction.class)).forEach(filterList::add)))
////                                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(properties.getMaxInMemorySize()))
//                                                .clientConnector(reactorClientHttpConnector);
//                                        if(StringUtils.hasText(baseUrl)){
//                                            builder.baseUrl(baseUrl);
//                                        }
//                                        Optional.ofNullable(properties.getAuthenticationKey())
//                                                .ifPresent(key1 ->
//                                                        Optional.ofNullable(properties.getAuthenticationValue()).ifPresent(value -> builder.filter(basicAuthentication(key1,value))));
//
//                                        log.info("构建【{}】webClient成功",entry.getKey());
//                                        clientCacheMap.putIfAbsent(entry.getKey(),builder.build());
//                                    });
////                            entries.stream()
////                                    .filter(Objects::nonNull)
////                                    .forEach(entry -> {
////                                        log.info("构建【{}】webClient开始",entry.getKey());
////                                        WebClientCustomProperties properties = entry.getValue();
////                                        String baseUrl = Optional.ofNullable(properties.getBaseUrl()).orElse(properties.getDefaultUriVariables());
////                                        ReactorClientHttpConnector reactorClientHttpConnector;
////                                        if(!properties.getUseGlobalResources()){
////                                            reactorClientHttpConnector = Optional.ofNullable(properties.getReactorResourceFactoryBeanName())
////                                                    .map(beanName -> applicationContext.getBean(beanName, ReactorClientHttpConnector.class))
////                                                    .orElse(null);
////                                        }else {
////                                            reactorClientHttpConnector = new ReactorClientHttpConnector();
////                                        }
////                                        WebClient.Builder builder = WebClient.builder()
////                                                .defaultCookies(cookieList ->
////                                                        Optional.ofNullable(properties.getCookies())
////                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> cookieList.add(cookie.getKey(), cookie.getValue()))))
////                                                .defaultHeaders(headList ->
////                                                        Optional.ofNullable(properties.getHeaders())
////                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> headList.add(cookie.getKey(), cookie.getValue()))))
////                                                .filters(filterList -> Optional.ofNullable(properties.getFilterNames())
////                                                        .ifPresent(filterNames -> filterNames.stream().map(name -> applicationContext.getBean(name, ExchangeFilterFunction.class)).forEach(filterList::add)))
//////                                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(properties.getMaxInMemorySize()))
////                                                .clientConnector(reactorClientHttpConnector);
////                                        if(StringUtils.hasText(baseUrl)){
////                                            builder.baseUrl(baseUrl);
////                                        }
////                                        Optional.ofNullable(properties.getAuthenticationKey())
////                                                .ifPresent(key1 ->
////                                                        Optional.ofNullable(properties.getAuthenticationValue()).ifPresent(value -> builder.filter(basicAuthentication(key1,value))));
////
////                                        log.info("构建【{}】webClient成功",entry.getKey());
////                                        clientCacheMap.putIfAbsent(entry.getKey(),builder.build());
////                                    });
//                        }
//                );
//        log.info("创建web请求客户端缓存完成!总共花费时间：{} 秒", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
//    }
//
//    public ApplicationContext getApplicationByName(@NonNull String name){
//        return Optional.ofNullable(clientCacheMap.get(name))
//                .orElseThrow(() -> {
//                    log.error("获取 {} ApplicationContext失败",name);
//                    return new RuntimeException(String.format("获取 %s ApplicationContext失败",name));
//                });
//    }
//
//
//    /**
//     */
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}
