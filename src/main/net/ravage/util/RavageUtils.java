package net.ravage.util;

import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.net.URL;

public class RavageUtils
{
    public static String readJsonFromUrl(String url) throws IOException {
        StringBuilder parsedContentFromUrl = new StringBuilder();
        URL urlObject = new URL(url);
        URLConnection uc = urlObject.openConnection();
        uc.connect();
        uc = urlObject.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        uc.getInputStream();
        InputStream is = uc.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        int ch;
        while ((ch = in.read()) != -1) {
            parsedContentFromUrl.append((char)ch);
        }
        return parsedContentFromUrl.toString();
    }
    
    public static String md5File(File file) {
        try {
            sendMessageInConsole("Getting md5 of " + file.getAbsolutePath());
            return DigestUtils.md5Hex(FileUtils.readFileToByteArray(file));
        }
        catch (IOException e) {
            return "";
        }
    }
    
    public static void crash(String crashTitle, String crashMessage) {
        JOptionPane.showMessageDialog(null, crashMessage, crashTitle, 0);
        System.exit(0);
    }
    
    public static void sendError(String errorTitle, String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, errorTitle, 0);
    }
    
    public static boolean isInteger(String s) {
        boolean isValid = true;
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException nfe) {
            isValid = false;
        }
        return isValid;
    }
    
    public static void sendMessageInConsole(String message, boolean isError) {
        PrintStream stream = System.out;
        if (isError) {
            stream = System.err;
        }
        stream.println("[" + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "] [Launcher] " + message);
    }
    
    public static void sendMessageInConsole(String message) {
        sendMessageInConsole(message, false);
    }
}
