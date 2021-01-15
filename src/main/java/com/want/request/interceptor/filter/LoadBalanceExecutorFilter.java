package com.want.request.interceptor.filter;


import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;

import java.net.URI;

/**
 * 负载均衡的执行拦截器
 *
 * @author WangZhiJian
 * @since 2021/1/14
 */
public interface LoadBalanceExecutorFilter extends EasySpringRestSendRequestExecutorFilter {
    /**
     * 将老的uri转换成新的uri
     * @param uri 老的uri
     * @return
     */
    URI doSelect(URI uri);

}
