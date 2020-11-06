package net.ravage.update.minecraft;

import java.io.IOException;
import com.google.gson.JsonSyntaxException;
import net.ravage.update.minecraft.utils.HttpUtils;
import java.net.Proxy;
import net.ravage.update.minecraft.utils.JsonManager;
import net.ravage.update.minecraft.versions.VersionsLoader;
import net.ravage.BaseLauncher;
import net.ravage.launch.GameType;
import net.ravage.update.minecraft.versions.CompleteVersion;
import java.net.URL;

public interface Version
{
    String getMCVersion();
    
    Game update();
    
    URL getURL();
    
    CompleteVersion getCompleteVersion();
    
    default GameType getGameType() {
        String str = this.getMCVersion().substring(2);
        Double d = Double.parseDouble(str);
        if (d <= 7.2) {
            return GameType.V1_7_2_LOWER;
        }
        if (d == 7.1) {
            return GameType.V1_7_10;
        }
        if (d >= 1.8) {
            return GameType.V1_8_HIGHER;
        }
        return GameType.V1_13_HIGHER_FORGE;
    }
    
    static Version build(BaseLauncher launcher) {
        try {
            VersionsLoader versionsLoader = new VersionsLoader();
            versionsLoader.loadOfficialVersions();
            LightVersion version = (LightVersion)versionsLoader.getVersion(launcher.getMinecraftVersion());
            versionsLoader.loadAllForgeVersion();
            if (launcher.getForgeVersion() != null && versionsLoader.containsForgeVersion(launcher.getForgeVersion())) {
                version.setForgeVersion(versionsLoader.getLoadedForgeVersion(launcher.getForgeVersion()));
            }
            CompleteVersion complete = null;
            try {
                complete = JsonManager.getGson().<CompleteVersion>fromJson(HttpUtils.performGet(version.getUrl(), Proxy.NO_PROXY), CompleteVersion.class);
            }
            catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
            }
            version.setCompleteVersion(complete);
            CompleteVersion finalComplete = complete;
            return new Version() {
                @Override
                public String getMCVersion() {
                    return launcher.getMinecraftVersion();
                }
                
                @Override
                public Game update() {
                    return version.update(launcher);
                }
                
                @Override
                public URL getURL() {
                    return version.getUrl();
                }
                
                @Override
                public CompleteVersion getCompleteVersion() {
                    return finalComplete;
                }
            };
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public enum VersionType
    {
        VANILLA, 
        FORGE;
    }
}
