package com.want.request.interceptor.filter;


import com.want.request.interceptor.EasySpringRestSendRequestExecutorFilter;

import java.net.URI;

/**
 * @author WangZhiJian
 * @since 2021/1/14
 */
public interface LoadBalanceExecutorFilter extends EasySpringRestSendRequestExecutorFilter {

    URI doSelect(URI uri);

}
