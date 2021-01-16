package com.want.spring.cloud.register.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author want
 * @createTime 2021.01.16.13:56
 */
@SpringBootApplication
@EnableEurekaServer
public class RegisterCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisterCenterApplication.class,args);
    }
}
