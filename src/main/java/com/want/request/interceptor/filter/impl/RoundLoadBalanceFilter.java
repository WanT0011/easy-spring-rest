package com.want.request.interceptor.filter.impl;

import com.want.request.interceptor.filter.AbstractLoadBalanceExecutorFilter;
import com.want.request.loadbalance.EasySpringRestLoadBalance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangZhiJian
 * @since 2021/1/14
 */
public class RoundLoadBalanceFilter extends AbstractLoadBalanceExecutorFilter {

    private DiscoveryClient discoveryClient;

    private EasySpringRestLoadBalance easySpringRestLoadBalance;


    private AtomicInteger curCount = new AtomicInteger(0);

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
        String suffix = oldPath.substring(0, oldPath.indexOf(host) + host.length());
        try {
            return new URI(instance.getUri()+suffix);
        } catch (URISyntaxException e) {
            throw new RuntimeException(String.format("构造{}URI失败！",instance.getUri()+suffix),e);
        }
    }


}
