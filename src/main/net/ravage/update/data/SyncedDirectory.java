package net.ravage.update.data;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import net.ravage.util.RavageUtils;
import java.util.Objects;
import net.ravage.update.EazyUpdater;
import java.io.File;
import java.util.List;

public class SyncedDirectory
{
    private String relativePath;
    private List<String> filesMd5;
    
    public SyncedDirectory(String relativePath, List<String> filesMd5) {
        this.relativePath = relativePath;
        this.filesMd5 = filesMd5;
    }
    
    public String getRelativePath() {
        return this.relativePath;
    }
    
    public List<String> getFilesMd5() {
        return this.filesMd5;
    }
    
    public void removeSurplus(File baseDir, EazyUpdater updater) {
        File dir = new File(baseDir, this.relativePath);
        if (dir.listFiles() != null) {
            for (File f : Objects.<File[]>requireNonNull(dir.listFiles(), "Pas fou")) {
                if (f.isFile() && !this.filesMd5.contains(RavageUtils.md5File(f)) && !updater.getProfile().getIgnoreList().contains(f.getAbsolutePath(), updater)) {
                    try {
                        RavageUtils.sendMessageInConsole("Deleting " + f.getAbsolutePath());
                        FileUtils.forceDelete(f);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
