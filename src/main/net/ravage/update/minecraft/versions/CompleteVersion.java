package net.ravage.update.minecraft.versions;

import java.util.HashSet;
import java.util.Set;
import java.io.File;
import net.ravage.update.minecraft.utils.OperatingSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.ravage.update.minecraft.LightVersion;

public class CompleteVersion extends LightVersion
{
    private String inheritsFrom;
    private List<Library> libraries;
    private String mainClass;
    private String incompatibilityReason;
    private String assets;
    private String jar;
    private CompleteVersion savableVersion;
    private transient boolean synced;
    private Downloads downloads;
    private AssetIndexInfo assetIndex;
    
    public CompleteVersion() {
        this.synced = false;
    }
    
    public String getID() {
        return this.getId();
    }
    
    public List<Library> getLibraries() {
        return this.libraries;
    }
    
    public String getMainClass() {
        return this.mainClass;
    }
    
    public String getJar() {
        return (this.jar == null) ? this.getId() : this.jar;
    }
    
    public Collection<Library> getRelevantLibraries() {
        List<Library> result = new ArrayList<Library>();
        for (Library library : this.libraries) {
            result.add(library);
        }
        return result;
    }
    
    public Collection<File> getClassPath(OperatingSystem os, File base) {
        Collection<Library> libraries = this.getRelevantLibraries();
        Collection<File> result = new ArrayList<File>();
        for (Library library : libraries) {
            if (library.getNative() == null) {
                result.add(new File(base, "libraries/" + library.getArtifactPath()));
            }
        }
        result.add(new File(base, "versions/" + this.getJar() + "/" + this.getJar() + ".jar"));
        return result;
    }
    
    public Set<String> getRequiredFiles(OperatingSystem os) {
        Set<String> neededFiles = new HashSet<String>();
        for (Library library : this.getRelevantLibraries()) {
            if (library.getNative() != null) {
                String natives = library.getNative();
                if (natives == null) {
                    continue;
                }
                neededFiles.add("libraries/" + library.getArtifactPath(natives));
            }
            else {
                neededFiles.add("libraries/" + library.getArtifactPath());
            }
        }
        return neededFiles;
    }
    
    @Override
    public String toString() {
        return "CompleteVersion{id='" + this.getID() + '\'' + ", libraries=" + this.libraries + ", mainClass='" + this.mainClass + '\'' + ", jar='" + this.jar + '}';
    }
    
    public String getIncompatibilityReason() {
        return this.incompatibilityReason;
    }
    
    public boolean isSynced() {
        return this.synced;
    }
    
    public void setSynced(boolean synced) {
        this.synced = synced;
    }
    
    public void parseDownloads() {
    }
    
    public AssetIndexInfo getAssetIndex() {
        if (this.assetIndex == null) {
            if (this.assets == null) {
                this.assetIndex = new AssetIndexInfo("legacy");
            }
            else {
                this.assetIndex = new AssetIndexInfo(this.assets);
            }
        }
        return this.assetIndex;
    }
    
    public Downloads getDownloads() {
        return this.downloads;
    }
}
