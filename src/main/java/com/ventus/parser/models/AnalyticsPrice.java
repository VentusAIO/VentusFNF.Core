package com.ventus.parser.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, ставиться над классом,
 * обозначает, что в конфигурацию нужно добавить проверку по изменению Price.
 * Реализацию analysePrice, можно посмотреть в AnalyticsExample.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnalyticsPrice {
}
