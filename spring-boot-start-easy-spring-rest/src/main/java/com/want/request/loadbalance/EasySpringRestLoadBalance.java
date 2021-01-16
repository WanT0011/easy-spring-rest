package com.want.request.loadbalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * 负载均衡策略
 *
 * @author want
 * @createTime 2021.01.11.21:51
 */
public interface EasySpringRestLoadBalance {

    /**
     * 进行服务的选择
     * @param loadBalance 负载均衡的策略
     * @param serviceName 服务名称
     * @param serverList 所有服务列表
     * @param excludeList 排除的服务列表
     * @return 服务的实例
     */
    ServiceInstance doSelect(EasySpringRestLoadBalance loadBalance,String serviceName, List<ServiceInstance> serverList, List<ServiceInstance> excludeList);

}
