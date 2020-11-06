package net.ravage.update.minecraft;

import java.io.IOException;

import net.ravage.update.minecraft.utils.FileUtils;
import net.ravage.update.minecraft.versions.AssetIndexInfo;
import java.io.InputStream;
import net.ravage.update.minecraft.versions.AssetDownloadable;
import java.util.Map;
import net.ravage.update.minecraft.utils.JsonManager;
import net.ravage.update.minecraft.versions.AssetIndex;
import org.apache.commons.io.IOUtils;
import java.net.MalformedURLException;
import net.ravage.update.minecraft.download.DownloadEntry;
import java.net.URL;
import net.ravage.update.minecraft.versions.Library;
import net.ravage.update.minecraft.versions.ForgeVersion;
import net.ravage.update.minecraft.versions.CompleteVersion;
import net.ravage.BaseLauncher;
import java.io.File;
import java.util.ArrayList;
import net.ravage.update.minecraft.download.DownloadManager;
import net.ravage.util.MinecraftFolder;

public class VersionDownloader
{
    private MinecraftFolder folder;
    private DownloadManager manager;
    private ArrayList<File> natives;
    
    public VersionDownloader(BaseLauncher launcher) {
        this.folder = launcher.getMinecraftFolder();
        this.manager = new DownloadManager(this.folder);
        this.natives = new ArrayList<File>();
    }
    
    public Game downloadVersion(BaseLauncher launcher, LightVersion version, CompleteVersion complete) {
        String index = this.downloadResources(launcher, complete, this.folder.getAssetsFolder());
        this.downloadClient(launcher, complete);
        Game game = new Game(complete.getID(), null, null, index, this.folder, Version.VersionType.VANILLA);
        if (version.getForgeVersion() != null) {
            this.downloadForgeLibraries(launcher, version.getForgeVersion());
            complete.setType(Version.VersionType.FORGE);
            game.setVersionType(Version.VersionType.FORGE);
            game.setTweaker(version.getForgeVersion().getTweakers());
        }
        this.downloadLibraries(launcher, complete);
        return game;
    }
    
    private void downloadForgeLibraries(BaseLauncher launcher, ForgeVersion version) {
        if (version.getLibraries() != null && !version.getLibraries().isEmpty()) {
            for (Library library : version.getLibraries()) {
                try {
                    if (library.getURL().contains("typesafe") || (version.getMinecraftVersion().equals("1.7.10") && library.getName().contains("guava") && library.getName().contains("15"))) {
                        continue;
                    }
                    if (version.getMinecraftVersion().equals("1.7.2") && library.getName().contains("launchwrapper")) {
                        this.manager.addDownloadableFile(new DownloadEntry(launcher, new URL("https://libraries.minecraft.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar"), new File(this.folder.getLibrariesFolder(), library.getArtifactURL()), null));
                    }
                    else {
                        this.manager.addDownloadableFile(new DownloadEntry(launcher, new URL(library.getURL()), new File(this.folder.getLibrariesFolder(), library.getArtifactURL()), null));
                    }
                }
                catch (MalformedURLException e2) {
                    e2.printStackTrace();
                }
            }
            return;
        }
        String v = version.getMinecraftVersion();
        if (!v.equals("1.1") && !v.startsWith("1.2.") && !v.equals("1.3.2") && !v.startsWith("1.4.")) {
            try {
                File l = new File(this.folder.getLibrariesFolder(), version.getJarMods()[0].getArtifactURL());
                this.manager.addDownloadableFile(new DownloadEntry(launcher, new URL(version.getJarMods()[0].getDownloads().getArtifact().getUrl()), new File(FileUtils.removeExtension(l.getAbsolutePath()) + ".jar"), version.getJarMods()[0].getDownloads().getArtifact().getSha1()));
                this.folder.setClientJarSHA1(null);
            }
            catch (MalformedURLException e3) {
                e3.printStackTrace();
            }
            return;
        }
        try {
            this.folder.setClientJarURL(new URL(version.getJarMods()[0].getDownloads().getArtifact().getUrl()));
            this.folder.setClientJarSHA1(version.getJarMods()[0].getDownloads().getArtifact().getSha1());
        }
        catch (MalformedURLException e3) {
            e3.printStackTrace();
        }
    }
    
    private void downloadLibraries(BaseLauncher launcher, CompleteVersion complete) {
        ArrayList<File> natives = new ArrayList<File>();
        for (Library library : complete.getLibraries()) {
            if (!complete.getID().equals("1.7.10") || !library.getName().contains("guava") || !complete.getVersionType().equals(Version.VersionType.FORGE)) {
                String file = "";
                String classifier = null;
                File local = new File(this.folder.getLibrariesFolder(), String.valueOf(library.getArtifactBaseDir()) + ".jar");
                String sha1 = null;
                if (library.getNative() != null) {
                    classifier = library.getNative();
                    if (classifier != null) {
                        file = library.getArtifactPath(classifier);
                        local = new File(this.folder.getNativesFolder(), library.getArtifactFilename(classifier));
                        natives.add(local);
                        this.manager.getDontDownload().add(local.getAbsolutePath());
                        if (library.getClassifiers() != null) {
                            if (classifier.contains("windows")) {
                                sha1 = library.getClassifiers().getWindows().getSha1();
                            }
                            if (classifier.contains("linux")) {
                                sha1 = library.getClassifiers().getLinux().getSha1();
                            }
                            if (classifier.contains("macos")) {
                                sha1 = library.getClassifiers().getMacos().getSha1();
                            }
                        }
                        else if (library.getDownloads().getArtifact() != null) {
                            sha1 = library.getDownloads().getArtifact().getSha1();
                        }
                    }
                }
                else {
                    file = library.getArtifactPath();
                }
                try {
                    URL url = new URL("https://libraries.minecraft.net/" + file);
                    if (library.getDownloads().getArtifact() != null) {
                        sha1 = library.getDownloads().getArtifact().getSha1();
                    }
                    this.manager.addDownloadableFile(new DownloadEntry(launcher, url, local, sha1));
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        this.natives.addAll(natives);
    }
    
    private String downloadResources(BaseLauncher launcher, CompleteVersion complete, File assets) {
        InputStream stream = null;
        File objectsFolder = new File(assets, "objects");
        File indexesFolder = new File(assets, "indexes");
        if (!objectsFolder.exists()) {
            objectsFolder.mkdirs();
        }
        AssetIndexInfo indexInfo = complete.getAssetIndex();
        File indexFile = new File(indexesFolder, String.valueOf(indexInfo.getId()) + ".json");
        try {
            URL indexUrl = indexInfo.getURL();
            stream = indexUrl.openConnection().getInputStream();
            String json = IOUtils.toString(stream);
            org.apache.commons.io.FileUtils.writeStringToFile(indexFile, json);
            AssetIndex index = JsonManager.getGson().<AssetIndex>fromJson(json, AssetIndex.class);
            for (Map.Entry<AssetIndex.AssetObject, String> entry : index.getUniqueObjects().entrySet()) {
                AssetIndex.AssetObject object = entry.getKey();
                String filename = String.valueOf(object.getHash().substring(0, 2)) + "/" + object.getHash();
                File file = new File(objectsFolder, filename);
                if (!file.isFile() || file.length() != object.getSize()) {
                    AssetDownloadable downloadable = new AssetDownloadable(launcher, entry.getValue(), object, "http://resources.download.minecraft.net/", objectsFolder);
                    this.manager.addDownloadableFile(downloadable);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return indexInfo.getId();
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
        IOUtils.closeQuietly(stream);
        return indexInfo.getId();
    }
    
    public void unzipNatives() {
        for (File file : this.natives) {
            if (file.exists()) {
                try {
                    FileUtils.unzip(file, this.folder.getNativesFolder());
                    this.manager.getDontDownload().add(file.getAbsolutePath());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File meta = new File(this.folder.getNativesFolder(), "META-INF");
        File md = new File(meta, "MANIFEST.MF");
        if (meta.exists()) {
            FileUtils.removeFolder(meta);
            FileUtils.removeFolder(meta);
        }
        if (md.exists()) {
            md.delete();
        }
    }
    
    private void downloadClient(BaseLauncher launcher, CompleteVersion complete) {
        try {
            if (this.folder.getClientJarURL() != null) {
                this.manager.addDownloadableFile(new DownloadEntry(launcher, this.folder.getClientJarURL(), this.folder.getClientJarFile(), complete.getDownloads().getClient().getSha1()));
            }
            else {
                this.manager.addDownloadableFile(new DownloadEntry(launcher, new URL(complete.getDownloads().getClient().getUrl()), this.folder.getClientJarFile(), complete.getDownloads().getClient().getSha1()));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public DownloadManager getDownloadManager() {
        return this.manager;
    }
}
