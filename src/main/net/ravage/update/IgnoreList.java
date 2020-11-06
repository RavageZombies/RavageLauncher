package net.ravage.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import net.ravage.util.RavageUtils;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class IgnoreList
{
    private HashMap<String, String> paths;
    
    public IgnoreList(String toReadURL) {
        this.paths = new HashMap<String, String>();
        String content = null;
        try {
            content = RavageUtils.readJsonFromUrl(toReadURL);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        JsonArray array = new JsonParser().parse(content).getAsJsonArray();
        AtomicReference<JsonObject> object = new AtomicReference<>();
        array.forEach(jsonElement -> {
            object.set(jsonElement.getAsJsonObject());
            this.paths.put(object.get().get("relativePath").getAsString(), object.get().get("type").getAsString());
        });
    }
    
    public boolean contains(String absolutePath, EazyUpdater updater) {
        String relativeLocalPath = absolutePath.replace("\\", "/").substring(updater.getDirectory().getAbsolutePath().length());
        for (String s : this.paths.keySet()) {
            if (relativeLocalPath.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
