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
import java.util.concurrent.atomic.AtomicBoolean;

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
            Type listType = new TypeToken<List<DownloadableResource>>() {}.getType();
            System.out.println(this.serverUrl + "index.php?action=listfiles&baselink=" + serverUrl);
            this.filesToDownload = JsonUtils.getGson().fromJson(RavageUtils.readJsonFromUrl(this.serverUrl + "index.php?action=listfiles&baselink=" + serverUrl), listType);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<SyncedDirectory> iterator = this.syncedDirectories.iterator();
        AtomicBoolean isPathAlreadyContained = new AtomicBoolean(false);
        this.filesToDownload.forEach(f -> {
            syncedDirectories.add(new SyncedDirectory(f.getRelativePath(), new ArrayList<>()));
        });
        this.syncedDirectories.forEach(d -> this.filesToDownload.forEach(f -> {
            if (f.getRelativePath().equals(d.getRelativePath())) {
                d.getFilesMd5().add(f.getMd5());
            }
        }));
        this.ignoreList = new IgnoreList(this.serverUrl + "index.php?action=ignorelist");
    }

    public IgnoreList getIgnoreList() {
        return this.ignoreList;
    }
}
