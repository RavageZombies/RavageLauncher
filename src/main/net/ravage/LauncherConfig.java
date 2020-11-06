package net.ravage;

import com.google.gson.JsonPrimitive;
import java.io.IOException;
import com.google.gson.JsonParser;
import net.ravage.util.RavageUtils;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LauncherConfig
{
    private HashMap<String, JsonElement> ELEMENTS;
    
    public LauncherConfig() {
        this.ELEMENTS = new HashMap<String, JsonElement>();
    }
    
    public void read( String url) {
        this.ELEMENTS.clear();
        try {
            String json = RavageUtils.readJsonFromUrl(url);
            AtomicReference<JsonElement> jsonElement = new AtomicReference<>();
            new JsonParser().parse(json).getAsJsonObject().entrySet().forEach(entry -> jsonElement.set(this.ELEMENTS.put(entry.getKey(), (JsonElement) entry.getValue())));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public JsonElement get(String key, JsonElement defaultValue) {
        return this.ELEMENTS.getOrDefault(key, defaultValue);
    }
    
    public String getString(String key) {
        return this.get(key, new JsonPrimitive("")).getAsString();
    }
    
    public boolean getBoolean(String key) {
        return this.get(key, new JsonPrimitive(Boolean.valueOf(false))).getAsBoolean();
    }
    
    public int getInteger(String key) {
        return this.get(key, new JsonPrimitive(0)).getAsInt();
    }
    
    public double getDouble(String key) {
        return this.get(key, new JsonPrimitive(0)).getAsDouble();
    }
    
    public float getFoat(String key) {
        return this.get(key, new JsonPrimitive(0)).getAsFloat();
    }
}
