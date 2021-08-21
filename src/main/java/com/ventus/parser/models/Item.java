package com.ventus.parser.models;

import lombok.Data;

/**
 * Класс Item нужен для хранения информации о каждой вещи.
 */
@Data
public class Item {
    /**
     * id или pid вещи
     */
    private String id;
    /**
     * Имя вещи
     */
    private String name;
    /**
     * Дополнительная информация
     */
    private String info;
    /**
     * url на вещь
     */
    private String url;
    /**
     * url на фото вещи
     */
    private String photo_url;
    /**
     * цена на вещь
     */
    private int price;
    /**
     * Количество на складе
     */
    private int available;
    /**
     * Состояние наличия в магазине
     */
    private boolean inStock;

    public Item(String id, String name, int price, int available, boolean inStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
        this.inStock = inStock;
    }
}
