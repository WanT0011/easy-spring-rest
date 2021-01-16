package com.want.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * 标注客户端为负载均衡的客户端
 *
 * @author want
 * @createTime 2021.01.11.21:33
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface WantLoadBalance {
    /**
     * 是否进行懒加载 -- TODO目前暂未做实现
     * @return
     */
    boolean lazy() default true;
}
