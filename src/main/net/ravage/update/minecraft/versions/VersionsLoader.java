package net.ravage.update.minecraft.versions;

import java.util.List;
import net.ravage.update.minecraft.utils.HttpUtils;
import java.net.Proxy;
import java.io.IOException;
import com.google.gson.JsonSyntaxException;
import net.ravage.update.minecraft.Version;
import java.net.MalformedURLException;
import net.ravage.update.minecraft.utils.JsonManager;
import net.ravage.update.minecraft.LightVersion;
import java.util.ArrayList;
import java.net.URL;
import com.google.gson.Gson;

public class VersionsLoader
{
    private Gson gson;
    private URL manifestURL;
    private URL forgeManifestURL;
    private ArrayList<String> loadedGlobalVersion;
    private ArrayList<LightVersion> versions;
    private ArrayList<ForgeVersion> forgeVersions;
    private ArrayList<ForgeVersion> loadedForgeVersions;
    
    public VersionsLoader() throws MalformedURLException {
        this.versions = new ArrayList<LightVersion>();
        this.forgeVersions = new ArrayList<ForgeVersion>();
        this.loadedForgeVersions = new ArrayList<ForgeVersion>();
        if (JsonManager.getGson() == null) {
            JsonManager.setGson(new Gson());
            JsonManager.init();
        }
        this.gson = JsonManager.getGson();
        this.manifestURL = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        this.forgeManifestURL = new URL("https://meta.multimc.org/v1/net.minecraftforge/index.json");
        this.loadedGlobalVersion = new ArrayList<String>();
    }
    
    public void loadOfficialVersions() {
        try {
            RawVersionList versionList = this.gson.<RawVersionList>fromJson(this.getContent(this.manifestURL), RawVersionList.class);
            for (LightVersion version : versionList.getVersions()) {
                version.setType(Version.VersionType.VANILLA);
                this.versions.add(version);
            }
        }
        catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public net.ravage.update.minecraft.versions.Version getVersion(String name) {
        for (LightVersion version : this.versions) {
            if (version.getId().equals(name)) {
                return version;
            }
        }
        return null;
    }
    
    public void loadAllForgeVersion() {
        try {
            RawForgeVersionList versionList = this.gson.<RawForgeVersionList>fromJson(this.getContent(this.forgeManifestURL), RawForgeVersionList.class);
            for (ForgeVersion version : versionList.getVersions()) {
                String[] v = version.getMinecraftVersion().split("\\.");
                int i = new Integer(v[1]);
                if (i >= 6) {
                    if (this.loadedGlobalVersion.contains(version.getMinecraftVersion())) {
                        version.setLatest(false);
                    }
                    else {
                        this.loadedGlobalVersion.add(version.getMinecraftVersion());
                        version.setLatest(true);
                    }
                    this.forgeVersions.add(version);
                }
            }
        }
        catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean containsForgeVersion(String id) {
        for (ForgeVersion version : this.forgeVersions) {
            if (version.getForgeVersionName().equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    public ForgeVersion getLoadedForgeVersion(String id) {
        for (ForgeVersion version : this.loadedForgeVersions) {
            if (version.getForgeVersionName().equals(id)) {
                return version;
            }
        }
        return this.loadForgeVersion(id);
    }
    
    public ForgeVersion getForgeVersion(String id) {
        for (ForgeVersion version : this.forgeVersions) {
            if (version.getForgeVersionName().equals(id)) {
                return version;
            }
        }
        return null;
    }
    
    public ForgeVersion loadForgeVersion(String id) {
        if (this.containsForgeVersion(id)) {
            ForgeVersion version = this.getForgeVersion(id);
            try {
                URL url = new URL("https://meta.multimc.org/v1/net.minecraftforge/" + version.getForgeVersionName() + ".json");
                version = this.gson.<ForgeVersion>fromJson(this.getContent(url), ForgeVersion.class);
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (JsonSyntaxException e2) {
                e2.printStackTrace();
            }
            catch (IOException e3) {
                e3.printStackTrace();
            }
            version.setType(Version.VersionType.FORGE);
            this.loadedForgeVersions.add(version);
            return version;
        }
        System.err.println("Unknow forge version: " + id);
        return null;
    }
    
    public String getContent(URL url) throws IOException {
        return HttpUtils.performGet(url, Proxy.NO_PROXY);
    }
    
    private class RawVersionList
    {
        private List<LightVersion> versions;
        
        private RawVersionList() {
            this.versions = new ArrayList<LightVersion>();
        }
        
        public List<LightVersion> getVersions() {
            return this.versions;
        }
    }
    
    private class RawForgeVersionList
    {
        private List<ForgeVersion> versions;
        
        private RawForgeVersionList() {
            this.versions = new ArrayList<ForgeVersion>();
        }
        
        public List<ForgeVersion> getVersions() {
            return this.versions;
        }
    }
}
