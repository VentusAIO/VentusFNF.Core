package com.ventus.parser.models;

/**
 * Интерфейс IAnalytic, нужен для реализации аналитики на сервере.
 */
public interface IAnalytic {
    /**
     * @param item_old передается оригинальный предмет
     * @param item_new передается измененный предмет
     * @return возвращает true, если надо уведомить об изменении, иначе false.
     */
    boolean analyse(Item item_old, Item item_new);
}
