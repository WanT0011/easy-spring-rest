package com.want;

import com.want.config.WebClientConfig;
import com.want.config.WebClientCustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Slf4j
@ConditionalOnProperty("com.want.web")
@EnableConfigurationProperties(WebClientConfig.class)
public class EasySpringRestAutoConfiguration implements SmartInitializingSingleton, ApplicationContextAware {

    @Resource
    private WebClientConfig webClientConfig;

    private ApplicationContext applicationContext;

    private Map<String, WebClient> clientCacheMap;

    /**
     */
    @Override
    public void afterSingletonsInstantiated() {
        log.info("创建web请求客户端缓存开始!");
        long start = System.currentTimeMillis();

        Optional.ofNullable(webClientConfig.getClientConig())
                .map(Map::entrySet)
                .filter(set -> !CollectionUtils.isEmpty(set))
                .ifPresent(
                        entries ->{
                                clientCacheMap = new ConcurrentHashMap<>(entries.size() * 4 / 3 + 1 );
                                entries.stream()
                                    .filter(Objects::nonNull)
                                    .forEach(entry -> {
                                        WebClientCustomProperties properties = entry.getValue();
                                        String baseUrl = Optional.ofNullable(properties.getBaseUrl()).orElse(properties.getDefaultUriVariables());
                                        ReactorClientHttpConnector reactorClientHttpConnector;
                                        if(!properties.getUseGlobalResources()){
                                            reactorClientHttpConnector = Optional.ofNullable(properties.getReactorResourceFactoryBeanName())
                                                    .map(beanName -> applicationContext.getBean(beanName, ReactorClientHttpConnector.class))
                                                    .orElse(null);
                                        }else {
                                            HttpClient httpClient = HttpClient.create(ConnectionProvider.builder(entry.getKey())
                                                    .pendingAcquireTimeout(Duration.of(properties.getDefaultRequestTimeOut(), SECONDS))
                                                    .maxConnections(properties.getMaxConnection())
                                                    .maxIdleTime(Duration.of(properties.getMaxIdleTime(), SECONDS))
                                                    .maxLifeTime(Duration.of(properties.getMaxLifeTime(), SECONDS))
                                                    .metrics(properties.getEnableMetrics())
                                                    .build());
                                            reactorClientHttpConnector = new ReactorClientHttpConnector(httpClient);
                                        }
                                        WebClient webClient = WebClient.builder()
                                                .baseUrl(baseUrl)
                                                .defaultCookies(cookieList ->
                                                        Optional.ofNullable(properties.getCookies())
                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> cookieList.add(cookie.getKey(), cookie.getValue()))))
                                                .defaultHeaders(headList ->
                                                        Optional.ofNullable(properties.getHeaders())
                                                                .map(Map::entrySet).ifPresent(entryList -> entryList.forEach(cookie -> headList.add(cookie.getKey(), cookie.getValue()))))
                                                .filters(filterList -> Optional.ofNullable(properties.getFilterNames())
                                                        .ifPresent(filterNames -> filterNames.stream().map(name -> applicationContext.getBean(name, ExchangeFilterFunction.class)).forEach(filterList::add)))
                                                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(properties.getMaxInMemorySize()))
                                                .clientConnector(reactorClientHttpConnector)
                                                .build();
                                        clientCacheMap.putIfAbsent(entry.getKey(),webClient);
                                });
                        }
                );
        log.info("创建web请求客户端缓存完成!总共花费时间：{} 秒", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
