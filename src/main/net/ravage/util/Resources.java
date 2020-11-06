package net.ravage.util;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Resources
{
    private static HashMap<String, BufferedImage> IMAGE_CACHE;
    
    public static URL getResource(String path) {
        return Resources.class.getClassLoader().getResource(path);
    }
    
    public static BufferedImage getImage(String path, boolean reload) {
        try {
            if (reload || !Resources.IMAGE_CACHE.containsKey(path)) {
                BufferedImage img = ImageIO.read(getResource(path));
                Resources.IMAGE_CACHE.put(path, img);
                return img;
            }
            return Resources.IMAGE_CACHE.get(path);
        }
        catch (IOException e) {
            return null;
        }
    }
    
    public static BufferedImage getImage(String path) {
        return getImage(path, false);
    }
    
    public static BufferedImage getImageFromWeb(String url, boolean reload) {
        try {
            if (reload || !Resources.IMAGE_CACHE.containsKey(url)) {
                BufferedImage img = ImageIO.read(new URL(url));
                Resources.IMAGE_CACHE.put(url, img);
                return img;
            }
            return Resources.IMAGE_CACHE.get(url);
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    public static BufferedImage getImageFromWeb(String url) {
        return getImageFromWeb(url, false);
    }
    
    static {
        IMAGE_CACHE = new HashMap<String, BufferedImage>();
    }
}
