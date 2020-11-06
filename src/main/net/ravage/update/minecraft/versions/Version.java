package net.ravage.update.minecraft.versions;

import net.ravage.update.minecraft.Game;
import net.ravage.BaseLauncher;
import java.net.URL;
import java.util.Date;

public class Version
{
    private String id;
    private Date time;
    private Date releaseTime;
    private URL url;
    private net.ravage.update.minecraft.Version.VersionType type;
    
    public String getId() {
        return this.id;
    }
    
    public Date getUpdatedTime() {
        return this.time;
    }
    
    public void setUpdatedTime(Date time) {
        this.time = time;
    }
    
    public Date getReleaseTime() {
        return this.releaseTime;
    }
    
    public void setReleaseTime(Date time) {
        this.releaseTime = time;
    }
    
    public URL getUrl() {
        return this.url;
    }
    
    public void setUrl(URL url) {
        this.url = url;
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    public net.ravage.update.minecraft.Version.VersionType getType() {
        return this.type;
    }
    
    public void setType(net.ravage.update.minecraft.Version.VersionType type) {
        this.type = type;
    }
    
    public Game update(BaseLauncher launcher) {
        return null;
    }
}
