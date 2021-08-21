package com.ventus.parser;

import com.ventus.parser.examples.AnalyticsExample;
import com.ventus.parser.models.*;
import com.ventus.parser.modules.Http;
import com.ventus.parser.modules.Loader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.ventus.parser.modules.Sender.registry;


public class ParserApplication {
    private static final List<IDownload> iDownloadList = new ArrayList<>();
    private static final List<IAnalytic> iAnalyticList = new ArrayList<>();
    public static String name = "no name";
    public static Loader loader = new Loader();
    private static IParse iparse = null;
    private static IExecute iexecute = null;
    public static int port = 8092;
    public static String ip = "localhost:"+port;
    public static String myip = "localhost:8092";

    /**
     * Метод для запуска сервера
     *
     * @param primarySource - class в котором храниться вся конфигурация
     * @param args          - аргументы командной строки
     */
    @SuppressWarnings("unchecked")
    public static void run(Class<?> primarySource, String[] args) {
        if (!primarySource.isAnnotationPresent(Parser.class)) {
            System.out.println("Ueban forgot annotation @Parse");
        } else {
            System.out.println("Class annotated; name  -  " + primarySource.getAnnotation(Parser.class));
            try {
                name = primarySource.getAnnotation(Parser.class).name();
                Method parse_method = primarySource.getMethod("parse", String.class);
                parse_method.setAccessible(true);
                iparse = html -> {
                    try {
                        if (parse_method.getAnnotatedReturnType().getType().getTypeName().contains("java.util.List")) {
                            parse_method.setAccessible(true);
                            return (List<Item>) parse_method.invoke(primarySource, html);
                        } else if (parse_method.getAnnotatedReturnType().getType() == Item.class) {
                            List<Item> items = new ArrayList<>();
                            parse_method.setAccessible(true);
                            items.add((Item) parse_method.invoke(primarySource, html));
                            return items;
                        } else {
                            System.out.println(parse_method.getAnnotatedReturnType().getType().getTypeName());
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                };
                Class[] parameters = parse_method.getParameterTypes();
                if (parameters.length == 1 && parameters[0].toString().equals("class java.lang.String")) {
                    System.out.println("You made everything clear");
                } else {
                    System.out.println("Parse method incorrectly implemented");
                    System.exit(-1);
                }
            } catch (NoSuchMethodException e) {
                System.out.println("Ueban implemented method incorrectly");
                e.printStackTrace();
                System.exit(-1);
            }


            if (primarySource.isAnnotationPresent(AnalyticsAvailable.class)) {
                iAnalyticList.add(AnalyticsExample::analyseAvailable);
            }
            if (primarySource.isAnnotationPresent(AnalyticsInStoke.class)) {
                iAnalyticList.add(AnalyticsExample::analyseInStoke);
            }
            if (primarySource.isAnnotationPresent(AnalyticsPrice.class)) {
                iAnalyticList.add(AnalyticsExample::analysePrice);
            }


            Method[] methods = primarySource.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(Download.class)) {
                    if (m.getAnnotatedReturnType().getType() == String.class) {
                        IDownload idownload = url -> {
                            try {
                                m.setAccessible(true);
                                return (String) m.invoke(primarySource, url);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return null;
                        };
                        iDownloadList.add(idownload);
                    } else {
                        System.out.println("Incorrect IDownload implementation");
                    }
                }
                if (m.isAnnotationPresent(Analytics.class)) {
                    if (m.getAnnotatedReturnType().getType() == boolean.class) {
                        IAnalytic iAnalytic = (item_old, item_new) -> {
                            try {
                                m.setAccessible(true);
                                return (boolean) m.invoke(primarySource, item_old, item_new);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return false;
                        };
                        iAnalyticList.add(iAnalytic);
                    } else {
                        System.out.println("Incorrect IAnalytic implementation");
                    }
                }
                if (m.isAnnotationPresent(Loop.class)) {
                    if (m.getAnnotatedReturnType().getType() == void.class) {
                        iexecute = (list, analyticsModule) -> {
                            try {
                                m.setAccessible(true);
                                m.invoke(primarySource, list, analyticsModule);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        };
                    } else {
                        System.out.println("Incorrect ILoop implementation");
                    }
                }
            }

            System.out.println("All OK \\/");
            System.out.println("Start application...");
            Http http = new Http(name);
            http.run();
            if (iparse != null) {
                start();
            } else {
                System.out.println("parse is missing");
            }
        }
    }


    private static void start() {
        List<String> urls = null;
        int i = 1;
//        while (true){
//            urls = getUrlList(name);
//            if(urls != null){
//                break;
//            }
//            try {
//                Thread.sleep(1000);
//                System.out.println("Retrying to get the configuration: " + i++);
//            } catch (InterruptedException ignored){}
//        }
        while (true) {
            if (registry(ip, name, myip)) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                    System.out.println("Retrying to get the configuration: " + i++);
                } catch (InterruptedException ignored) {
                }
            }
        }
        loader.setList(new ArrayList<>());
        loader.setIparse(iparse);
        loader.setIexecute(iexecute);
        loader.setIDownloadList(iDownloadList);
        loader.setIAnalyticList(iAnalyticList);
        System.out.println("Wait database update...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Application Started");
        loader.setStart(true);
        while (true) {
            try {
                loader.execute();
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                    System.out.println("Items missing");
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * @param name - имя сайта
     * @return List<String> с url по которым будет производиться парсинг
     */
    private static List<String> getUrlList(String name) {
        try {
            URL url = new URL("http://localhost:8080/shop?marketName=" + name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            List<String> list = new ArrayList<>();
            while ((line = rd.readLine()) != null) {
                list.add(line.substring(2, line.length() - 2));
            }
            for (String l : list) {
                System.out.println(l);
            }
            rd.close();
            return list;
        } catch (Exception ignored) {
        }
        return null;
    }
}
