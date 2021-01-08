package com.want.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Data
public class WebClientCustomProperties {
    // 自定义uri的基础路径
    private String baseUrl;
    // 扩展URI模板时使用的默认值
    private String defaultUriVariables;
    // 每个请求的header
    private Map<String,String> headers;
    // 针对每个请求的Cookie。
    private Map<String,String> cookies;
    // 针对每个请求的客户端过滤器 bean 名称
    private List<String> filterNames;
    //
    private String exchangeStrategies;
    // 参与持有的全局Reactor Netty资源包括事件循环线程和连接池。这是推荐的模式，
    // 因为固定的共享资源是事件循环并发的首选。在这种模式下，全局资源将保持活动状态，直到进程退出
    private Boolean useGlobalResources = true;

    private String reactorResourceFactoryBeanName;
    // 编解码器具有在内存中缓冲数据的限制，以避免应用程序内存问题。默认情况下，这些设置为256KB。如果这还不够，您将收到以下错误
    // org.springframework.core.io.buffer.DataBufferLimitException：超出最大可缓存字节数限制
    private Integer maxInMemorySize = 256 * 1024;
    // 连接超时时间， 毫秒作为单位
    private Long connectionTimeOut = 1 * 1000L;
    // 请求超时时间
    private Long defaultRequestTimeOut = 2L;

    private int maxConnection = 30;

    private int maxIdleConnection = 5;
    // 最大空闲时间 s
    private int maxIdleTime = 30 * 60;
    // 最大存活时间
    private int maxLifeTime = 4 * 60 * 60;
    // enableMetrics
    private Boolean enableMetrics = false;
}
