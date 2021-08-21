package com.ventus.parser.modules;

import com.ventus.parser.models.*;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ventus.parser.examples.DownloadExample.getHTML;


/**
 * Реализация алгоритма Round Robbin для постоянной обработки url'ов,
 * полученных из конфигурации.
 */
public class Loader {
    public final long NUM_OF_TASKS = 1;
    private final int NUM_OF_THREAD = 2;
    @Setter
    private List<String> list = new ArrayList<>();
    @Setter
    private IParse iparse;
    @Setter
    private IExecute iexecute;
    private Loop loop;
    @Setter
    private List<IDownload> iDownloadList = new ArrayList<>();
    @Setter
    private AnalyticsModule analyticsModule;
    @Setter
    private boolean isStart = false;

    /**
     * Запуск бесконечного цикла, который будет обрабатывать запросы.
     */
    public void execute() {
        if (iexecute != null) {
            loop = new Loop(list);
            loop.start();
        } else {
            Balancer<String> balancer = new Balancer<>();
            for (int numOfThread = 1; numOfThread < NUM_OF_THREAD; numOfThread = numOfThread + 10) {
                balancer.init(list);
                final ExecutorService executor = Executors.newFixedThreadPool(numOfThread);
                Runnable runnable = () -> {
                    String s = balancer.getNext();
                    //Socket socket = ProxyManager.get();
                    // get если он есть, иначе страндартное решение из библиотеки
                    String bodyHTML;
                    int i = 0;
//                while(true){
//                    try{
//                        if(i == iDownloadList.size()-1){
//                            i = 0;
//                        }
//                        if (iDownloadList.isEmpty()) {
//                            bodyHTML = getHTML(s);
//                        } else {
//                            bodyHTML = iDownloadList.get(i).get(s);
//                        }
//                        break;
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        System.out.println(s);
//                        System.out.println(i);
//                        i++;
//                    }
//                }
                    if (iDownloadList.isEmpty()) {
                        bodyHTML = getHTML(s);
                    } else {
                        bodyHTML = iDownloadList.get(i).get(s);
                    }
                    try {
                        List<Item> items = iparse.parse(bodyHTML);
                        if (items != null) {
                            for (Item item : items) {
                                System.out.println(item.toString());
                            }
                            System.out.println("Отправляю на аналитику");
                            analyticsModule.analyse(items);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        list.remove(s);
                        balancer.init(list);
                    }
                };
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < NUM_OF_TASKS; i++) {
                    executor.execute(runnable);
                }
                executor.shutdown();
                while (!executor.isTerminated()) {
                }
            }
        }
    }

    public void addURL(String url) {
        this.list.add(url);
        if (iexecute != null && isStart) {
            loop.interrupt();
            loop = new Loop(list);
            loop.start();
        }
    }

    public void setIAnalyticList(List<IAnalytic> iAnalyticList) {
        analyticsModule = new AnalyticsModule(iAnalyticList);
    }

    class Loop extends Thread {
        private List<String> list;

        public Loop(List<String> list) {
            this.list = list;
        }

        public void run() {
            iexecute.execute(this.list, analyticsModule);
        }
    }
}
