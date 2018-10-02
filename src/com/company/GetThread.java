package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private String login;
    private final Gson gson;
    private int n;

    public GetThread(String login) {
        super();
        this.login = login;
        gson = new GsonBuilder().create();
    }

    public GetThread() {
        super();
        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?login=" + login + "&from=" + n);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();

                try {
                    byte[] buf = RequestWoker.requestBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            System.out.println(m);
                            n++;
                        }
                    }
                } finally {
                    is.close();
                }

                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
