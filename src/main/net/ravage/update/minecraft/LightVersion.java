package net.ravage.update.minecraft;

import net.ravage.update.UpdateManager;
import net.ravage.BaseLauncher;
import java.net.URL;
import net.ravage.update.minecraft.versions.ForgeVersion;
import net.ravage.update.minecraft.versions.CompleteVersion;
import net.ravage.update.minecraft.versions.Version;

public class LightVersion extends Version
{
    private CompleteVersion completeVersion;
    private ForgeVersion forgeVersion;
    
    public void setId(String id) {
        super.setID(id);
    }
    
    @Override
    public void setUrl(URL url) {
        super.setUrl(url);
    }
    
    @Override
    public void setType(net.ravage.update.minecraft.Version.VersionType type) {
        super.setType(type);
    }
    
    public void setForgeVersion(ForgeVersion forgeVersion) {
        this.forgeVersion = forgeVersion;
    }
    
    public ForgeVersion getForgeVersion() {
        return this.forgeVersion;
    }
    
    @Override
    public URL getUrl() {
        return super.getUrl();
    }
    
    @Override
    public String getId() {
        return super.getId();
    }
    
    public net.ravage.update.minecraft.Version.VersionType getVersionType() {
        return super.getType();
    }
    
    public CompleteVersion getCompleteVersion() {
        return this.completeVersion;
    }
    
    public void setCompleteVersion(CompleteVersion completeVersion) {
        this.completeVersion = completeVersion;
    }
    
    @Override
    public Game update(BaseLauncher launcher) {
        UpdateManager manager = launcher.getUpdateManager();
        manager.setMinecraftVersionDownloader(new VersionDownloader(launcher));
        Game game = manager.getMinecraftVersionDownloader().downloadVersion(launcher, this, this.getCompleteVersion());
        manager.getMinecraftVersionDownloader().getDownloadManager().check();
        manager.getMinecraftVersionDownloader().getDownloadManager().startDownload();
        manager.getMinecraftVersionDownloader().unzipNatives();
        manager.getMinecraftVersionDownloader().getDownloadManager().check();
        return game;
    }
}
