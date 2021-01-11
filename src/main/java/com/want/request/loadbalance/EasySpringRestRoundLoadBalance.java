package com.want.request.loadbalance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author want
 * @createTime 2021.01.11.22:03
 */
public class EasySpringRestRoundLoadBalance implements EasySpringRestLoadBalance {

    private AtomicInteger curIndex = new AtomicInteger(0);

    @Override
    public ServiceInstance doSelect(EasySpringRestLoadBalance loadBalance, List<ServiceInstance> serverList, List<ServiceInstance> excludeList) {
       return Optional.ofNullable(loadBalance)
                .map(
                        lb ->
                            Optional.ofNullable(serverList)
                                    .filter(sl -> !CollectionUtils.isEmpty(sl))
                                    .map(sl -> sl.toArray(new ServiceInstance[0]))
                                    .map(
                                            serverArray -> serverArray[curIndex.incrementAndGet()%serverArray.length]
                                    ).orElse(null)
                ).orElse(null);
    }


}
