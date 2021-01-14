package com.want.exception;

import com.alibaba.fastjson.JSON;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * @author WangZhiJian
 * @since 2021/1/14
 */
public class ReqUriIsNullException extends RuntimeException {

    public ReqUriIsNullException(ClientRequest request) {
        super(String.format("请求为空,请求为：%s", JSON.toJSONString(request)));
    }

}
