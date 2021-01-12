package com.want;

import com.want.annotation.WantLoadBalance;
//import com.want.factory.EasyWebClientFactory;
import com.want.request.client.EasySpringRestClient;
import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author WangZhiJian
 * @since 2021/1/8
 */
@Slf4j
//@Import(EasyWebClientFactory.class)
public class EasySpringRestAutoConfiguration{

    @Autowired(required = false)
    @WantLoadBalance
    private List<EasySpringRestClient> loadBalanceWebClient = Collections.emptyList();


    @Resource
    private DiscoveryClient discoverClient;


//    @ConditionalOnMissingBean(EasySpringRestSendRequestExecutorFilter.class)
//    public SmartInitializingSingleton initEasySpringRestSendRequestInterceptor(){
//        return () -> {
//            loadBalanceWebClient.forEach(
//                    client -> client.
//            );
//        }
//    }

}
