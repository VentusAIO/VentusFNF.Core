package com.ventus.parser;

import com.ventus.parser.models.Item;
import com.ventus.parser.modules.Http;
import com.ventus.parser.modules.Sender;

import java.util.Map;

public class Test {
    public static void main(String[] args) {
        System.out.println("All OK \\/");
        System.out.println("Start application...");
        ParserApplication.name = "citilink";
        Http http = new Http("citilink");
        http.run();
        Sender.registry("localhost:8090", "citilink", "localhost:8092");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(Map.Entry<String, String> e : Sender.output_servers.entrySet()){
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        Item item1 = new Item("p123", "qwerty", 1234556, 0, false);
        Sender.send_item(item1);
    }
}
