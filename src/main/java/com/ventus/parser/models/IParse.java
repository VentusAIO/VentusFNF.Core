package com.ventus.parser.models;

import java.util.List;


/**
 * Интерфейс IParse нужен, чтобы показать, как нужно реализовывать главный метод parse.
 */
public interface IParse {
    /**
     * @param html - как правило, это html страница, но может быть и json строка,
     *             если реализован собственный метод Download.
     * @return возвращает все предметы, которые были найдены на сайте.
     */
    List<Item> parse(String html);
}
