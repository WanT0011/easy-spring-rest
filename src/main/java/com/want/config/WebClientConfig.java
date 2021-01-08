package com.want.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Getter
@ConfigurationProperties("com.want.web")
public class WebClientConfig {

    private Map<String,WebClientCustomProperties> clientConfig = new ConcurrentHashMap<>(0);
}
