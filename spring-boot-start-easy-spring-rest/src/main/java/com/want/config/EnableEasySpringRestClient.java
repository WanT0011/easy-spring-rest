package com.want.config;

import com.want.EasySpringRestAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用EasySpringRest的注解
 *
 * @author want
 * @createTime 2021.01.11.21:29
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasySpringRestAutoConfiguration.class)
public @interface EnableEasySpringRestClient {
}
