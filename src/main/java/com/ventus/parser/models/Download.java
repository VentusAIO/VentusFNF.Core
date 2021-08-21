package com.ventus.parser.models;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для обозначения метода Download,
 * который реализует интерфейс IDownload,
 * для добавления его в итоговую конфигурацию сервера.
 */
@Target(value= ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Download {
}
