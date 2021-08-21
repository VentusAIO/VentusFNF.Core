package com.ventus.parser.models;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Главная аннотация, которая запускает сервер.
 * Необходимо реализовать метод IParse
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parser{
    /**
     * @return имя сервера для регистрации на main сервере.
     */
    String name() default "no name";
}
