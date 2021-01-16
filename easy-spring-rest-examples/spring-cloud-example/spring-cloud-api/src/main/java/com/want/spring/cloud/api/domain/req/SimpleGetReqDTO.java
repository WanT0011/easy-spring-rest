package com.want.spring.cloud.api.domain.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author want
 * @createTime 2021.01.16.14:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleGetReqDTO implements Serializable {
    /**
     * 服务器的当前时间
     */
    private LocalDateTime curTime;
    /**
     * 服务的监听的端口
     */
    private String localPort;
}
