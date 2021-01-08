package com.want;

import com.want.config.WebClientConfig;
import com.want.config.WebClientCustomProperties;
import com.want.factory.EasyWebClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Slf4j
//@ConditionalOnProperty("com.want.web")
@Import(EasyWebClientFactory.class)
@EnableConfigurationProperties(WebClientConfig.class)
public class EasySpringRestAutoConfiguration{
}
