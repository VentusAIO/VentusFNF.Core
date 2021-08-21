package com.ventus.parser.examples;


import com.ventus.parser.models.Item;

public class AnalyticsExample {
    public static boolean analyseInStoke(Item item_old, Item item_new) {
        return !item_old.isInStock() && item_new.isInStock();
    }

    public static boolean analyseAvailable(Item item_old, Item item_new) {
        return item_old.getAvailable() < item_new.getAvailable();
    }

    public static boolean analysePrice(Item item_old, Item item_new) {
        return item_old.getPrice() > item_new.getPrice();
    }
}
