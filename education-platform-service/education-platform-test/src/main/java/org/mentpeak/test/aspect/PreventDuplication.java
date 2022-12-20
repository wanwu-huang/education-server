package org.mentpeak.test.aspect;

import java.lang.annotation.*;

/**
  * 防止重复提交注解
  */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDuplication {

    /**
     * 防重复操作限时标记数值（存储redis限时标记数值）
     */
    String value() default "value" ;

    /**
     * 防重复操作过期时间（借助redis实现限时控制）
     */
    long expireSeconds() default 1;
}
