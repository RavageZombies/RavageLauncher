package net.ravage.update.minecraft.versions;

import org.apache.commons.io.IOUtils;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.commons.io.FileUtils;
import java.net.MalformedURLException;
import java.net.URL;
import net.ravage.BaseLauncher;
import java.io.File;
import net.ravage.update.minecraft.download.DownloadEntry;

public class AssetDownloadable extends DownloadEntry
{
    private AssetIndex.AssetObject asset;
    private File destination;
    
    public AssetDownloadable(BaseLauncher launcher, String name, AssetIndex.AssetObject asset, String url, File destination) throws MalformedURLException {
        super(launcher, new URL(String.valueOf(url) + createPathFromHash(asset.getHash())), null, null);
        this.asset = asset;
        this.destination = destination;
        this.setTarget(new File(destination, createPathFromHash(asset.getHash())));
    }
    
    protected static String createPathFromHash(String hash) {
        return String.valueOf(hash.substring(0, 2)) + "/" + hash;
    }
    
    @Override
    public void download() {
        try {
            File localAsset = super.getTarget();
            File localCompressed = this.asset.hasCompressedAlternative() ? new File(this.destination, createPathFromHash(this.asset.getCompressedHash())) : null;
            URL remoteAsset = this.getURL();
            URL url;
            if (this.asset.hasCompressedAlternative()) {
                StringBuilder sb = new StringBuilder(String.valueOf(this.getURL().toString()));
                url = new URL(sb.append(createPathFromHash(this.asset.getCompressedHash())).toString());
            }
            else {
                url = null;
            }
            URL remoteCompressed = url;
            this.ensureFileWritable(localAsset);
            if (localCompressed != null) {
                super.ensureFileWritable(localCompressed);
            }
            if (localAsset.isFile()) {
                if (FileUtils.sizeOfDirectory(localAsset) == this.asset.getSize()) {
                    return;
                }
                FileUtils.deleteDirectory(localAsset);
            }
            if (localCompressed != null && localCompressed.isFile()) {
                String localCompressedHash = DownloadEntry.getDigest(localCompressed, "SHA", 40);
                if (localCompressedHash.equalsIgnoreCase(this.asset.getCompressedHash())) {
                    this.decompressAsset(localAsset, localCompressed);
                    return;
                }
                FileUtils.deleteDirectory(localCompressed);
            }
            if (remoteCompressed != null && localCompressed != null) {
                HttpURLConnection connection = this.makeConnection(remoteCompressed);
                int status = connection.getResponseCode();
                if (status / 100 != 2) {
                    throw new RuntimeException("Server responded with " + status);
                }
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(localCompressed);
                String hash = this.copyAndDigest(inputStream, outputStream, "SHA", 40);
                if (hash.equalsIgnoreCase(this.asset.getCompressedHash())) {
                    this.decompressAsset(localAsset, localCompressed);
                    return;
                }
                FileUtils.deleteDirectory(localCompressed);
                throw new RuntimeException(String.format("Hash did not match downloaded compressed asset (Expected %s, downloaded %s)", this.asset.getCompressedHash(), hash));
            }
            else {
                HttpURLConnection connection = this.makeConnection(remoteAsset);
                int status = connection.getResponseCode();
                if (status / 100 != 2) {
                    throw new RuntimeException("Server responded with " + status);
                }
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(localAsset);
                String hash = this.copyAndDigest(inputStream, outputStream, "SHA", 40);
                if (hash.equalsIgnoreCase(this.asset.getHash())) {
                    return;
                }
                FileUtils.deleteDirectory(localAsset);
                throw new RuntimeException(String.format("Hash did not match downloaded asset (Expected %s, downloaded %s)", this.asset.getHash(), hash));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected HttpURLConnection makeConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
        connection.setRequestProperty("Expires", "0");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(30000);
        return connection;
    }
    
    protected void decompressAsset(File localAsset, File localCompressed) throws IOException {
        OutputStream outputStream = FileUtils.openOutputStream(localAsset);
        InputStream inputStream = new GZIPInputStream(FileUtils.openInputStream(localCompressed));
        String hash = null;
        try {
            hash = this.copyAndDigest(inputStream, outputStream, "SHA", 40);
        }
        finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
        IOUtils.closeQuietly(outputStream);
        IOUtils.closeQuietly(inputStream);
        if (hash.equalsIgnoreCase(this.asset.getHash())) {
            return;
        }
        FileUtils.deleteDirectory(localAsset);
        throw new RuntimeException("Had local compressed asset but unpacked hash did not match (expected " + this.asset.getHash() + " but had " + hash + ")");
    }
    
    @Override
    public boolean needDownload() {
        return !this.getTarget().exists();
    }
}
