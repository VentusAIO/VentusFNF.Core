package com.ventus.parser.modules;

import com.ventus.parser.models.IAnalytic;
import com.ventus.parser.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsModule {
    private static ExecutorService executorservice = Executors.newSingleThreadExecutor();
    private final Map<String, Item> map = new HashMap<>();
    private final List<IAnalytic> analyticList = new ArrayList<>();


    /**
     * @param analytics аналитические модули
     */
    AnalyticsModule(List<IAnalytic> analytics) {
        this.analyticList.addAll(analytics);
    }


    /**
     * @param items предметы, которые надо проанализировать
     */
    public void analyse(List<Item> items) {
        for (Item item : items) {
            analyse(item);
        }
    }

    /**
     * @param item предмет, которые надо проанализировать
     */
    public void analyse(Item item) {
        if (map.containsKey(item.getId())) {
            Item itemInMap = map.get(item.getId());
            if (itemInMap.getName().equals(item.getName())) {
                for (IAnalytic analytic : analyticList) {
                    if (analytic.analyse(itemInMap, item)) {
                        System.out.println("Sending...");
                        executorservice.execute(() -> Sender.send_item(item));
                    }
                }
            } else {
                System.out.println("Название не совпадает:");
                System.out.println("Старое:");
                System.out.println(item);
                System.out.println("Новое:");
                System.out.println(itemInMap);
                System.out.println("///////////////////////////////////");
            }
        }
        map.put(item.getId(), item);
    }


    /**
     * Добавление аналитического модуля
     *
     * @param analytic аналитический модуль
     */
    public void addIAnalytic(IAnalytic analytic) {
        this.analyticList.add(analytic);
    }

    /**
     * Добавление нескольких аналитических модулей
     *
     * @param analytics Массив аналитических модулей
     */
    public void addIAnalytics(List<IAnalytic> analytics) {
        this.analyticList.addAll(analytics);
    }
}
