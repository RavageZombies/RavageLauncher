package net.ravage.update.data;


import com.google.gson.reflect.TypeToken;
import net.ravage.update.IgnoreList;
import net.ravage.update.JsonUtils;
import net.ravage.util.RavageUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.io.IOException;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class UpdateProfile
{
    private String serverUrl;
    private File directory;
    public List<DownloadableResource> filesToDownload;
    public List<SyncedDirectory> syncedDirectories = new ArrayList<SyncedDirectory>();
    private IgnoreList ignoreList;

    public UpdateProfile(String serverUrl, File directory) {
        this.serverUrl = serverUrl;
        this.directory = directory;

        try {
            Type listType = (new TypeToken<List<DownloadableResource>>() {}).getType();
            this.filesToDownload = (List) JsonUtils.getGson().fromJson(RavageUtils.readJsonFromUrl(this.serverUrl + "index.php?action=listfiles&baselink=" + serverUrl), listType);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        this.filesToDownload.forEach((f) -> {
            boolean isPathAlreadyContained = false;
            Iterator var3 = this.syncedDirectories.iterator();

            while(var3.hasNext()) {
                SyncedDirectory syncedDirectory = (SyncedDirectory)var3.next();
                if (syncedDirectory.getRelativePath().equals(f.getRelativePath())) {
                    isPathAlreadyContained = true;
                }
            }

            if (!isPathAlreadyContained) {
                this.syncedDirectories.add(new SyncedDirectory(f.getRelativePath(), new ArrayList()));
            }

        });
        this.syncedDirectories.forEach((d) -> {
            this.filesToDownload.forEach((f) -> {
                if (f.getRelativePath().equals(d.getRelativePath())) {
                    d.getFilesMd5().add(f.getMd5());
                }

            });
        });
        this.ignoreList = new IgnoreList(this.serverUrl + "index.php?action=ignorelist");
    }

    public IgnoreList getIgnoreList() {
        return this.ignoreList;
    }
}
