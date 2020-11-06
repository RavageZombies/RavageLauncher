package net.ravage.update.minecraft.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class HttpUtils
{
    public static String performGet(URL url, Proxy proxy) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(60000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT x.y; rv:10.0) Gecko/20100101 Firefox/10.0");
        InputStream inputStream = connection.getInputStream();
        try {
            return IOUtils.toString(inputStream);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
