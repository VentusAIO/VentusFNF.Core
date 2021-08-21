package com.ventus.parser.modules;

import com.ventus.parser.ParserApplication;
import com.ventus.parser.models.Item;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Sender {
    public static final Map<String, String> output_servers = new HashMap<>();

    public static boolean registry(String ip, String name, String my_ip) {
        try {
            URL url = new URL("http://" + ip + "/parser/registry/" + name + "?ip=" + URLEncoder.encode(my_ip, UTF_8));
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection.getResponseCode() == 200;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean send_item(String ip, Item item) {
        try {
//            URL url = new URL("http://"+ip+"/send/"+ ParserApplication.name +"?name="+ URLEncoder.encode(item.getName(), UTF_8.toString()) +
//                    "&info=" + URLEncoder.encode(item.getInfo() == null ? "" : item.getInfo(), UTF_8) +
//                    "&url=" + URLEncoder.encode(item.getUrl() == null ? "" : item.getUrl(), UTF_8)+
//                    "&photo=" + URLEncoder.encode(item.getPhoto_url() == null ? "" : item.getPhoto_url(), UTF_8)+
//                    "&price=" + URLEncoder.encode(String.valueOf(item.getPrice()), UTF_8)+
//                    "&available=" + URLEncoder.encode(String.valueOf(item.getAvailable()), UTF_8)+
//                    "&inStock=" + URLEncoder.encode(String.valueOf(item.isInStock()), UTF_8));
//            System.out.println(url);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");


            String urlParameters = "name=" + URLEncoder.encode(item.getName(), "windows-1251") +
                    "&info=" + URLEncoder.encode((item.getInfo() == null ? "" : item.getInfo()), "windows-1251") +
                    "&url=" + URLEncoder.encode((item.getUrl() == null ? "" : item.getUrl()), UTF_8) +
                    "&photo=" + URLEncoder.encode((item.getPhoto_url() == null ? "" : item.getPhoto_url()), UTF_8) +
                    "&price=" + item.getPrice() +
                    "&available=" + item.getAvailable() +
                    "&inStock=" + item.isInStock();

            HttpURLConnection connection = null;

            try {
                //Create connection
                URL url = new URL("http://" + ip + "/send/" + ParserApplication.name);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
//                DataOutputStream wr = new DataOutputStream (
//                        connection.getOutputStream());
                DataOutputStream tmpStream = new DataOutputStream(connection.getOutputStream());
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(tmpStream, "UTF-8"));
                wr.write(urlParameters);
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return connection.getResponseCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return connection.getResponseCode() == 200;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void send_item(Item item) {
        for (Map.Entry<String, String> entry : output_servers.entrySet()) {
            System.out.println("Sending: " + entry.getValue());
            send_item(entry.getValue(), item);
        }
    }
}
