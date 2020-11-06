package net.ravage.update.minecraft.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class JsonManager
{
    private static Gson gson;
    
    public static Gson init() {
        return new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    }
    
    public JsonManager() {
        JsonManager.gson = init();
    }
    
    public static Gson getGson() {
        return JsonManager.gson;
    }
    
    public static void setGson(Gson gson) {
        JsonManager.gson = gson;
    }
}
