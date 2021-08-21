package com.ventus.parser.models;

/**
 * Интерфейс IDownload нужен для собственной реализации получения контента.
 */
public interface IDownload {
    /**
     * @param url - передается url, с которым можно что-то сделать
     * @return возвращает полученные данные
     */
    String get(String url);
}
