package com.ventus.parser.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Предоставляет балансировку парсинга данных
 * на основе Round-Robin
 * */
public class Balancer<WebResource> {

    //Коллекция тасков на парсинг
    private final List<WebResource> webList = new ArrayList<>();
    private Iterator<WebResource> it;
    //атомик для контроял позиции парсинга
    private final AtomicInteger position = new AtomicInteger(0);
    private volatile int positionInt = 0;
    private boolean isInit = false;
    private volatile int listSize;
    private int MAX_COUNTER;
    private WebResource single;

    public Balancer(){}

    public synchronized void init(List<WebResource> objects) {
        if (!isInit) {
            webList.clear();
            webList.addAll(objects);
            single = objects.get(0);
            it =  webList.iterator();
            listSize = webList.size();
            MAX_COUNTER = webList.size();
            isInit = true;
        }
    }

    public void reInit() {
        isInit = false;
        init(webList);
    }

    public Collection<WebResource> getXAIURLList() {
        return webList;
    }

    public WebResource getNext() {
        return webList.get(getNextPosition() % listSize);
    }

    public WebResource getNextOblListSize() {
        return webList.get(getNextPosition());
    }

    public final int getNextPosition() {
        while (true){
            int current = position.get();
            int next = current + 1;
            if(next >= MAX_COUNTER){
                next = 0;
            }
            if (position.compareAndSet(current, next))
                return current;
        }
    }


    public synchronized WebResource getNext_Iterator(){
        if (!it.hasNext()) {
            it = webList.iterator();
        }
        return it.next();
    }

    public synchronized WebResource getNext_Long() {
        positionInt++;
        if(positionInt >= MAX_COUNTER){
            positionInt = 0;
        }
        return webList.get(positionInt);
    }

    public WebResource getNextSingle(){
        return single;
    }
}
