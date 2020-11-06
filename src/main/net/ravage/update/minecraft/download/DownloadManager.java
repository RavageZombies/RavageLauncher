package net.ravage.update.minecraft.download;

import net.ravage.update.minecraft.utils.FileUtils;
import java.io.File;

import net.ravage.util.RavageUtils;
import java.util.ArrayList;
import net.ravage.util.MinecraftFolder;

public class DownloadManager
{
    private MinecraftFolder folder;
    public ArrayList<DownloadEntry> filesToDownload;
    public ArrayList<DownloadEntry> downloadedFile;
    private ArrayList<String> dontDelete;
    private int value;
    
    public DownloadManager(MinecraftFolder folder) {
        this.filesToDownload = new ArrayList<DownloadEntry>();
        this.downloadedFile = new ArrayList<DownloadEntry>();
        this.dontDelete = new ArrayList<String>();
        this.folder = folder;
    }
    
    public DownloadEntry addDownloadableFile(DownloadEntry file) {
        if (!this.filesToDownload.contains(file)) {
            this.dontDelete.add(file.getDestination().getAbsolutePath());
            if (file.needDownload()) {
                this.filesToDownload.add(file);
            }
        }
        return file;
    }
    
    public void startDownload() {
        if (this.filesToDownload.size() == 0) {
            RavageUtils.sendMessageInConsole("[MinecraftDownloader] Nothing to download!", false);
            return;
        }
        this.value = this.filesToDownload.size();
        RavageUtils.sendMessageInConsole("[MinecraftDownloader] " + this.filesToDownload.size() + " files to download.", false);
        for (DownloadEntry download : this.filesToDownload) {
            RavageUtils.sendMessageInConsole("[MinecraftDownloader] Download: " + download.getURL() + " | " + this.getDownloadPercentage() + "% | " + this.downloadedFile.size() + "/" + this.filesToDownload.size(), false);
            download.download();
            this.downloadedFile.add(download);
        }
        RavageUtils.sendMessageInConsole("[MinecraftDownloader] Finished download !", false);
    }
    
    private int getPercentToDownload() {
        return this.value / this.filesToDownload.size() * 100;
    }
    
    public static int crossMult(int value, int maximum, int coefficient) {
        return value / maximum * coefficient;
    }
    
    public int removeSurplus(File folder) {
        ArrayList<File> files = FileUtils.listFilesForFolder(folder);
        int count = 0;
        for (File f : files) {
            if (f.isDirectory()) {
                boolean good = true;
                ArrayList<File> files2 = FileUtils.listFilesForFolder(f);
                if (files2.isEmpty()) {
                    f.delete();
                }
                else {
                    for (File f2 : files2) {
                        if (!f2.isDirectory()) {
                            good = true;
                            break;
                        }
                        good = false;
                    }
                    if (!good) {
                        f.delete();
                    }
                }
            }
            if (!this.dontDelete.contains(f.getAbsolutePath()) && !f.isDirectory()) {
                f.delete();
                if (folder == this.folder.getNativesFolder()) {
                    continue;
                }
                ++count;
            }
        }
        return count;
    }
    
    public void clearDownloads() {
        this.filesToDownload.clear();
        this.downloadedFile.clear();
        this.dontDelete.clear();
    }
    
    public float getDownloadPercentage() {
        return this.downloadedFile.size() / this.filesToDownload.size() * 100;
    }
    
    public boolean isDownloadFinished() {
        return this.filesToDownload.size() == 0;
    }
    
    public void check() {
        int count = 0;
        if (this.folder.getLibrariesFolder() != null && !this.folder.getLibrariesFolder().exists()) {
            this.folder.getLibrariesFolder().mkdirs();
        }
        if (this.folder.getNativesFolder() != null && !this.folder.getNativesFolder().exists()) {
            this.folder.getNativesFolder().mkdirs();
        }
        if (this.folder.getLibrariesFolder() != null) {
            count += this.removeSurplus(this.folder.getLibrariesFolder());
        }
        if (count != 0) {
            RavageUtils.sendMessageInConsole("[MinecraftDownloader]Deleted " + count + " files.", false);
        }
    }
    
    public ArrayList<String> getDontDownload() {
        return this.dontDelete;
    }
}
