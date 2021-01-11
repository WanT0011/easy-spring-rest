package com.want.config;

import com.want.EasySpringRestAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author want
 * @createTime 2021.01.11.21:29
 */
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EasySpringRestAutoConfiguration.class)
public @interface EnableEasySpringRestClient {
}