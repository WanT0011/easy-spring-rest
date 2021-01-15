package com.want;

import com.want.annotation.WantLoadBalance;
import com.want.request.client.EasySpringRestClient;
import com.want.request.interceptor.filter.LoadBalanceExecutorFilter;
import com.want.request.interceptor.filter.impl.RoundLoadBalanceFilter;
import com.want.request.loadbalance.EasySpringRestLoadBalance;
import com.want.request.loadbalance.EasySpringRestRoundLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;


/**
 * 自动配置类
 *
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Slf4j
@EnableDiscoveryClient
public class EasySpringRestAutoConfiguration{

    /**
     * 所有需要负载均衡的 客户端
     */
    @Autowired(required = false)
    @WantLoadBalance
    private List<EasySpringRestClient> loadBalanceWebClientList = Collections.emptyList();

    /**
     * 默认使用轮询的负载均衡策略
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(EasySpringRestLoadBalance.class)
    public EasySpringRestLoadBalance easySpringRestLoadBalance(){
        return new EasySpringRestRoundLoadBalance();
    }

    /**
     * 负载均衡的拦截器
     * @param discoveryClient 服务发现的客户端
     * @param easySpringRestLoadBalance 负载均衡策略
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(LoadBalanceExecutorFilter.class)
    public LoadBalanceExecutorFilter loadBalanceExecutorFilter(DiscoveryClient discoveryClient,EasySpringRestLoadBalance easySpringRestLoadBalance){
        return new RoundLoadBalanceFilter(discoveryClient,easySpringRestLoadBalance);
    }

    /**
     * 为所有的负载均衡客户端增加负载均衡的拦截器
     * @param loadBalanceExecutorFilter 负载均衡的拦截器
     * @return
     */
    @Bean
    public SmartInitializingSingleton easySpringRestClientInitializer(LoadBalanceExecutorFilter loadBalanceExecutorFilter){
        return () ->
            loadBalanceWebClientList.forEach(loadBalanceWebClient
                    -> loadBalanceWebClient.addFilterFirst(loadBalanceExecutorFilter));
    }

}
