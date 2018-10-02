package com.company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestWoker {
    protected static byte[] requestBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
    protected static String getResponse(String address) throws IOException{
            URL url = new URL(address);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            InputStream is = http.getInputStream();
            return new String(RequestWoker.requestBodyToArray(is), StandardCharsets.UTF_8);
    }
}
