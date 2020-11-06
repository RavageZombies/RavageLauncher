package net.ravage.update.data;

import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import net.ravage.util.RavageUtils;
import net.ravage.update.EazyUpdater;
import java.io.File;

public class DownloadableResource
{
    private String fileName;
    private String downloadUrl;
    private String relativePath;
    private String md5;
    private int size;
    
    public DownloadableResource(String fileName, String downloadUrl, String relativePath, String md5, int size) {
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.relativePath = relativePath;
        this.md5 = md5;
        this.size = size;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    
    public String getRelativePath() {
        return this.relativePath;
    }
    
    public String getMd5() {
        return this.md5;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void download(File baseDir, EazyUpdater updater) {
        baseDir.mkdir();
        new File(baseDir, this.getRelativePath()).mkdirs();
        File file = new File(new File(baseDir, this.getRelativePath()), this.getFileName());
        if (updater.getProfile().getIgnoreList().contains(file.getAbsolutePath(), updater)) {
            return;
        }
        if (file.exists()) {
            String md5 = RavageUtils.md5File(file);
            if (md5.equals(this.getMd5())) {
                updater.incrementDownloadedBytes(this.size);
                return;
            }
            try {
                FileUtils.forceDelete(file);
                RavageUtils.sendMessageInConsole("Deleting " + file.getAbsolutePath());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        RavageUtils.sendMessageInConsole("Starting download of " + this.getDownloadUrl());
        try (BufferedInputStream in = new BufferedInputStream(new URL(this.getDownloadUrl()).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                updater.incrementDownloadedBytes(1024L);
            }
        }
        catch (IOException ex) {}
    }
}
