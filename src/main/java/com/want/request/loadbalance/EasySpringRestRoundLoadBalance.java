package com.want.request.loadbalance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询的负载均衡策略
 *
 * @author want
 * @createTime 2021.01.11.22:03
 */
public class EasySpringRestRoundLoadBalance implements EasySpringRestLoadBalance {

    /**
     * 每个服务的当前下标
     */
    private final Map<String,AtomicInteger> curIndexMap = new ConcurrentHashMap<>();


    /**
     * 使用轮询进行服务的选择
     * 如果无存活实例返回空
     *
     * @param loadBalance 负载均衡的策略
     * @param serviceName 服务名称
     * @param serverList  所有服务列表
     * @param excludeList 排除的服务列表
     * @return 服务的实例
     */
    @Override
    public ServiceInstance doSelect(EasySpringRestLoadBalance loadBalance, String serviceName, List<ServiceInstance> serverList, List<ServiceInstance> excludeList) {
        return Optional.ofNullable(loadBalance)
                .map(
                        lb ->
                                Optional.ofNullable(serverList)
                                        .filter(sl -> !CollectionUtils.isEmpty(sl))
                                        .map(sl -> sl.toArray(new ServiceInstance[0]))
                                        .map(
                                                serverArray -> {
                                                    if(!curIndexMap.containsKey(serviceName)){
                                                        curIndexMap.put(serviceName,new AtomicInteger(0));
                                                    }
                                                    AtomicInteger curIndex = curIndexMap.get(serviceName);
                                                    return serverArray[curIndex.incrementAndGet()%serverArray.length];
                                                }
                                        ).orElse(null)
                ).orElse(null);
    }
}
