package com.want.spring.cloud.consumer;

import com.want.annotation.WantLoadBalance;
import com.want.config.EnableEasySpringRestClient;
import com.want.request.client.EasySpringRestClient;
import com.want.request.loadbalance.EasySpringRestLoadBalance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author want
 * @createTime 2021.01.16.18:33
 */
@EnableEasySpringRestClient
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class,args);
    }

    @Bean
    @WantLoadBalance
    public EasySpringRestClient demoSpringRestClient(){
        return new EasySpringRestClient.Builder().build();
    }
}
