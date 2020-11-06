package net.ravage.update;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

public class JsonUtils
{
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();;
    
    public static Gson getGson() {
        return JsonUtils.gson;
    }
}
