package net.ravage.update.minecraft;

import net.ravage.util.MinecraftFolder;

public class Game
{
    private String version;
    private String tweaker;
    private String mainClass;
    private String assetIndex;
    private MinecraftFolder folder;
    private Version.VersionType versionType;
    
    public Game(String version, String tweaker, String mainClass, String assetIndex, MinecraftFolder folder, Version.VersionType versionType) {
        this.version = version;
        this.tweaker = tweaker;
        this.mainClass = mainClass;
        this.assetIndex = assetIndex;
        this.folder = folder;
        this.versionType = versionType;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getTweaker() {
        return this.tweaker;
    }
    
    public void setTweaker(String tweaker) {
        this.tweaker = tweaker;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getAssetIndex() {
        return this.assetIndex;
    }
    
    public void setAssetIndex(String assetIndex) {
        this.assetIndex = assetIndex;
    }
    
    public MinecraftFolder getFolder() {
        return this.folder;
    }
    
    public void setFolder(MinecraftFolder folder) {
        this.folder = folder;
    }
    
    public Version.VersionType getVersionType() {
        System.out.println("Get version type: " + this.versionType);
        return this.versionType;
    }
    
    public void setVersionType(Version.VersionType versionType) {
        this.versionType = versionType;
    }
}
