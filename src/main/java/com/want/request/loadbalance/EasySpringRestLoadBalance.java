package com.want.request.loadbalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author want
 * @createTime 2021.01.11.21:51
 */
public interface EasySpringRestLoadBalance {

    ServiceInstance doSelect(EasySpringRestLoadBalance loadBalance, List<ServiceInstance> serverList, List<ServiceInstance> excludeList);

}
