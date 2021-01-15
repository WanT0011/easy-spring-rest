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
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Slf4j
@EnableDiscoveryClient
public class EasySpringRestAutoConfiguration{

    @Autowired(required = false)
    @WantLoadBalance
    private List<EasySpringRestClient> loadBalanceWebClientList = Collections.emptyList();


    @ConditionalOnMissingBean(EasySpringRestLoadBalance.class)
    public EasySpringRestLoadBalance easySpringRestLoadBalance(){
        return new EasySpringRestRoundLoadBalance();
    }

    @ConditionalOnMissingBean(LoadBalanceExecutorFilter.class)
    public LoadBalanceExecutorFilter loadBalanceExecutorFilter(DiscoveryClient discoveryClient,EasySpringRestLoadBalance easySpringRestLoadBalance){
        return new RoundLoadBalanceFilter(discoveryClient,easySpringRestLoadBalance);
    }

    @Bean
    public SmartInitializingSingleton easySpringRestClientInitializer(LoadBalanceExecutorFilter loadBalanceExecutorFilter){
        return () ->
            loadBalanceWebClientList.forEach(loadBalanceWebClient
                    -> loadBalanceWebClient.addFilterFirst(loadBalanceExecutorFilter));
    }

}
