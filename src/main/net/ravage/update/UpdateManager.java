package net.ravage.update;

import java.net.URL;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import net.ravage.util.JavaUtil;
import java.io.IOException;
import net.ravage.util.RavageUtils;
import net.ravage.update.minecraft.Version;
import net.ravage.update.minecraft.VersionDownloader;
import net.ravage.update.minecraft.Game;
import net.ravage.BaseLauncher;

public class UpdateManager
{
    private BaseLauncher launcher;
    private String updateMessage;
    private String jreLink;
    private String jreMd5;
    private Game game;
    private VersionDownloader minecraftVersionDownloader;
    private EazyUpdater updater;
    private Version version;
    private boolean isDownloading;
    private boolean isDownloadingMc;
    
    public UpdateManager(BaseLauncher launcher) {
        this.jreLink = null;
        this.jreMd5 = null;
        this.version = null;
        this.isDownloading = false;
        this.isDownloadingMc = false;
        this.launcher = launcher;
    }
    
    public String getUpdateMessage() {
        return this.updateMessage;
    }
    
    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }
    
    public void enableCustomJre(String downloadLink, String md5) {
        this.jreLink = downloadLink;
        this.jreMd5 = md5;
    }
    
    public boolean hasCustomJre() {
        return this.jreLink != null && this.jreMd5 != null;
    }
    
    public void setMinecraftVersionDownloader(VersionDownloader minecraftVersionDownloader) {
        this.minecraftVersionDownloader = minecraftVersionDownloader;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public VersionDownloader getMinecraftVersionDownloader() {
        return this.minecraftVersionDownloader;
    }
    
    public Version getVersionObj() {
        if (this.version == null) {
            RavageUtils.sendMessageInConsole("Getting the version...", false);
            this.version = Version.build(this.launcher);
        }
        return this.version;
    }

    public void updateAll() {
        this.isDownloading = true;
        this.downloadJava();
        if (!this.launcher.isMCP()) {
            this.downloadMc();
        }

        try {
            this.updater = new EazyUpdater(this.launcher.getUpdateServerUrl(), this.launcher.MINECRAFT_DIR);
            this.updater.startDownload(this);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        this.isDownloading = false;
    }

    public void downloadMc() {
        this.isDownloadingMc = true;
        this.updateMessage = "Verifying assets...";
        Version version = this.getVersionObj();
        this.updateMessage = "Downloading Minecraft...";
        this.setGame(version.update());
        this.isDownloadingMc = false;
    }

    public void downloadJava() {
        this.updateMessage = "Java verification";
        RavageUtils.sendMessageInConsole("V\u00e9rification de votre version de Java...");
        if (JavaUtil.hasJava64()) {
            RavageUtils.sendMessageInConsole("Vous avez Java 64 ! Tout est bon. ;)");
            return;
        }
        if (!this.hasCustomJre()) {
            RavageUtils.crash("Vous n'avez pas Java 64 bit !", "D\u00e9sol\u00e9, pour jouer \u00e0 Minecraft il faut avoir Java \n 64 bit.");
        }
        this.launcher.CUSTOM_JRE_DIR.mkdir();
        try {
            if (this.launcher.CUSTOM_JRE_ZIP.exists()) {
                String md5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(this.launcher.CUSTOM_JRE_ZIP));
                if (md5.equalsIgnoreCase(this.jreMd5)) {
                    RavageUtils.sendMessageInConsole("Vous avez la bonne version, \"fullgood\"");
                    return;
                }
                RavageUtils.sendMessageInConsole("Mauvaise version du JRE... Suppression du Custom-JRE actuel...", true);
                FileUtils.forceDelete(this.launcher.CUSTOM_JRE_ZIP);
                FileUtils.deleteDirectory(this.launcher.CUSTOM_JRE_DIR);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        this.updateMessage = "Downloading Java";
        try {
            RavageUtils.sendMessageInConsole("T\u00e9l\u00e9chargement de Java 64...");
            FileUtils.copyURLToFile(new URL(this.jreLink), this.launcher.CUSTOM_JRE_ZIP);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.updateMessage = "Unzipping JRE...";
        RavageUtils.sendMessageInConsole("Unzipping JRE..");
        try {
            net.ravage.update.minecraft.utils.FileUtils.unzip(this.launcher.CUSTOM_JRE_ZIP, this.launcher.CUSTOM_JRE_DIR);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isDownloadingMc() {
        return this.isDownloadingMc;
    }
    
    public boolean isDownloading() {
        return this.isDownloading;
    }

    public int getTotalFiles() {
        if (this.isDownloadingMc && this.minecraftVersionDownloader != null) {
            return this.minecraftVersionDownloader.getDownloadManager().filesToDownload.size();
        } else {
            return this.updater != null ? this.updater.getTotalFilesToDownload() : 0;
        }
    }

    public int getDownloadedFiles() {
        if (this.isDownloadingMc && this.minecraftVersionDownloader != null) {
            return this.minecraftVersionDownloader.getDownloadManager().downloadedFile.size();
        } else {
            return this.updater != null ? this.updater.getDownloadedFiles() : 0;
        }
    }

    public EazyUpdater getUpdater() {
        return updater;
    }
}
