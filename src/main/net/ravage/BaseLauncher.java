package net.ravage;

import net.ravage.launch.LaunchManager;
import net.ravage.update.UpdateManager;
import net.ravage.util.MinecraftFolder;

import java.io.File;
import java.net.URL;

public class BaseLauncher {
    public final File LAUNCHER_DIR;
    public final File MINECRAFT_DIR;
    public final File MODS_DIR;
    public final File RESOURCES_PACKS_DIR;
    public final File USER_INFOS;
    public final File CUSTOM_JRE_DIR;
    public final File CUSTOM_JRE_ZIP;
    private String updateServerUrl;
    private String minecraftVersion;
    private String forgeVersion;
    private boolean isMCP;
    private String launcherName;
    private UpdateManager updateManager;
    private LaunchManager launchManager;

    public BaseLauncher(String launcherName, String minecraftVersion, String forgeVersion, String updateServerUrl, boolean isMCP) {
        this.isMCP = false;
        this.minecraftVersion = minecraftVersion;
        this.forgeVersion = forgeVersion;
        this.updateServerUrl = updateServerUrl;
        this.isMCP = isMCP;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            this.LAUNCHER_DIR = new File(System.getenv("APPDATA"), "." + launcherName);
        } else if (os.contains("mac")) {
            this.LAUNCHER_DIR = new File(System.getenv("user.home") + "/Library/Application Support/", "." + launcherName);
        } else {
            this.LAUNCHER_DIR = new File(System.getenv("user.home"), "." + launcherName);
        }

        this.MINECRAFT_DIR = new File(this.LAUNCHER_DIR, "/");
        this.MODS_DIR = new File(this.MINECRAFT_DIR, "/mods/");
        this.RESOURCES_PACKS_DIR = new File(this.MINECRAFT_DIR, "/resourcepacks/");
        this.USER_INFOS = new File(this.LAUNCHER_DIR, "infos.json");
        this.CUSTOM_JRE_DIR = new File(this.LAUNCHER_DIR, "/jre-x64/");
        this.CUSTOM_JRE_ZIP = new File(this.CUSTOM_JRE_DIR, "jre-x64.zip");
        this.updateManager = new UpdateManager(this);
        this.launchManager = new LaunchManager(this);
    }

    public BaseLauncher(String launcherName, String minecraftVersion, String updateServerUrl, boolean isMCP) {
        this(launcherName, minecraftVersion, null, updateServerUrl, isMCP);
    }

    public UpdateManager getUpdateManager() {
        return this.updateManager;
    }

    public MinecraftFolder getMinecraftFolder() {
        return new MinecraftFolder(this.MINECRAFT_DIR, new File(this.MINECRAFT_DIR, "/assets/"), new File(this.MINECRAFT_DIR, "/libraries/"), new File(this.MINECRAFT_DIR, "/natives/"), new File(this.MINECRAFT_DIR, "minecraft.jar"), (URL)null, (String)null);
    }

    public String getForgeVersion() {
        return this.forgeVersion;
    }

    public String getMinecraftVersion() {
        return this.minecraftVersion;
    }

    public String getUpdateServerUrl() {
        return this.updateServerUrl;
    }

    public String getLauncherName() {
        return this.launcherName;
    }

    public LaunchManager getLaunchManager() {
        return this.launchManager;
    }

    public boolean isMCP() {
        return this.isMCP;
    }
}
