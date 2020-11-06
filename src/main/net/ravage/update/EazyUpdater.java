package net.ravage.update;

import net.ravage.util.RavageUtils;
import java.io.IOException;
import net.ravage.update.data.UpdateProfile;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class EazyUpdater
{
    private String serverUrl;
    private File directory;
    private UpdateProfile profile;
    private boolean willRemoveSurplus;
    private int totalFilesToDownload;
    private int downloadedFiles;
    private boolean downloadStarted;
    private boolean downloadFinished;
    private boolean checkingStarted;
    private boolean checkingFinished;
    private long totalBytesToDownload;
    private long downloadedBytes;
    
    public EazyUpdater(String serverUrl, File directory) throws IOException {
        this.willRemoveSurplus = true;
        this.downloadedFiles = 0;
        this.downloadStarted = false;
        this.downloadFinished = false;
        this.checkingStarted = false;
        this.checkingFinished = false;
        this.serverUrl = serverUrl;
        if (directory.isFile()) {
            throw new IOException("The specified directory is a file");
        }
        this.directory = directory;
        this.profile = new UpdateProfile(serverUrl, directory);
        long[] bytes = { 0L };
        this.profile.filesToDownload.forEach(f -> bytes[0] += f.getSize());
        this.totalBytesToDownload = bytes[0];
        this.totalFilesToDownload = this.profile.filesToDownload.size();
    }
    
    public UpdateProfile getProfile() {
        return this.profile;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public int getDownloadedFiles() {
        return this.downloadedFiles;
    }
    
    public int getTotalFilesToDownload() {
        return this.totalFilesToDownload;
    }
    
    public void startDownload(UpdateManager manager) {
        this.downloadStarted = true;
        this.checkingStarted = true;
        manager.setUpdateMessage("Checking files");
        RavageUtils.sendMessageInConsole("Checking files");
        if (this.willRemoveSurplus) {
            this.profile.syncedDirectories.forEach(dir -> dir.removeSurplus(this.directory, this));
        }
        this.checkingFinished = true;
        manager.setUpdateMessage("Downloading files...");
        RavageUtils.sendMessageInConsole("Downloading files...");
        this.profile.filesToDownload.forEach(file -> {
            file.download(this.directory, this);
            ++this.downloadedFiles;
            return;
        });
        this.downloadFinished = true;
    }
    
    public long getDownloadedBytes() {
        return this.downloadedBytes;
    }
    
    public void incrementDownloadedBytes(long val) {
        this.downloadedBytes += val;
    }
    
    public long getTotalBytesToDownload() {
        return this.totalBytesToDownload;
    }
    
    public boolean isDownloadFinished() {
        return this.downloadFinished;
    }
    
    public boolean isDownloadStarted() {
        return this.downloadStarted;
    }
    
    public boolean isCheckingStarted() {
        return this.checkingStarted;
    }
    
    public boolean isCheckingFinished() {
        return this.checkingFinished;
    }
    
    public void setWillRemoveSurplus(boolean willRemoveSurplus) {
        this.willRemoveSurplus = willRemoveSurplus;
    }
}
