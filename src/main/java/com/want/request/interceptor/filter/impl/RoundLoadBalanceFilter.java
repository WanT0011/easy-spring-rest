package com.want.request.interceptor.filter.impl;

import com.want.request.interceptor.filter.AbstractLoadBalanceExecutorFilter;
import com.want.request.loadbalance.EasySpringRestLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * TODO 负载均衡策略是由内部的{@link EasySpringRestLoadBalance}去做实现的，和本身无关，所以可否做个通用的
 * 轮询的负载均衡拦截器负载均衡
 *
 * @author WangZhiJian
 * @since 2021/1/14
 */
@Slf4j
public class RoundLoadBalanceFilter extends AbstractLoadBalanceExecutorFilter {
    /**
     * 服务发现客户端
     */
    private DiscoveryClient discoveryClient;
    /**
     * 负载均衡策略
     */
    private EasySpringRestLoadBalance easySpringRestLoadBalance;


    public RoundLoadBalanceFilter(DiscoveryClient discoveryClient,EasySpringRestLoadBalance easySpringRestLoadBalance) {
        this.discoveryClient = discoveryClient;
        this.easySpringRestLoadBalance = easySpringRestLoadBalance;
    }

    @Override
    public URI doSelect(URI uri)  {
        String host = uri.getHost();

        List<ServiceInstance> instances = discoveryClient.getInstances(host);
        if(CollectionUtils.isEmpty(instances)){
            throw new RuntimeException(String.format("%s 在注册中心无可用实例！",host));
        }

        ServiceInstance instance = easySpringRestLoadBalance.doSelect(easySpringRestLoadBalance, instances, Collections.EMPTY_LIST);

//        ServiceInstance instance;
//        if(instances.size() == 1){
//            instance = instances.get(0);
//        }else{
//            ServiceInstance[] serviceInstances = instances.toArray(new ServiceInstance[0]);
//            int curIndex = curCount.incrementAndGet();
//            instance = serviceInstances[curIndex%serviceInstances.length];
//        }
        String oldPath = uri.toString();
        String suffix = oldPath.substring(oldPath.indexOf(host) + host.length());
        try {
            log.info("Round load balance select uri is {}",instance.getUri()+suffix);
            return new URI(instance.getUri()+suffix);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("构造{}URI失败！",instance.getUri()+suffix),e);
        }
    }


}
