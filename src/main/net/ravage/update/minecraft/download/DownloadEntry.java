package net.ravage.update.minecraft.download;

import java.security.DigestInputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.io.Closeable;
import java.security.MessageDigest;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import net.ravage.update.minecraft.utils.FileUtils;
import net.ravage.BaseLauncher;
import net.ravage.util.MinecraftFolder;
import java.io.File;
import java.net.URL;

public class DownloadEntry
{
    private URL url;
    private File target;
    private File destination;
    private String sha1;
    private MinecraftFolder folder;
    
    public DownloadEntry(BaseLauncher launcher, URL url, File destination, String sha1) {
        this.url = url;
        this.destination = destination;
        this.sha1 = sha1;
        this.folder = launcher.getMinecraftFolder();
    }
    
    public boolean needDownload() {
        if (this.destination == null) {
            return false;
        }
        if (!this.destination.exists()) {
            return true;
        }
        if (this.sha1 == null) {
            return false;
        }
        if (FileUtils.getSHA1(this.destination).equalsIgnoreCase(this.sha1)) {
            return false;
        }
        if (!this.destination.getParentFile().getAbsolutePath().equals(this.folder.getNativesFolder().getAbsolutePath())) {
            this.destination.delete();
            return true;
        }
        return false;
    }
    
    public void download() {
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(this.url, this.destination);
        }
        catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
        }
    }
    
    public String copyAndDigest(InputStream inputStream, OutputStream outputStream, String algorithm, int hashLength) throws IOException {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            closeSilently(inputStream);
            closeSilently(outputStream);
            throw new RuntimeException("Missing Digest." + algorithm, e);
        }
        byte[] buffer = new byte[65536];
        try {
            for (int read = inputStream.read(buffer); read >= 1; read = inputStream.read(buffer)) {
                digest.update(buffer, 0, read);
                outputStream.write(buffer, 0, read);
            }
        }
        finally {
            closeSilently(inputStream);
            closeSilently(outputStream);
        }
        closeSilently(inputStream);
        closeSilently(outputStream);
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, digest.digest()));
    }
    
    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public void setTarget(File target) {
        this.target = target;
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public File getTarget() {
        return this.target;
    }
    
    protected void ensureFileWritable(File target) {
        if (target.getParentFile() != null && !target.getParentFile().isDirectory() && !target.getParentFile().mkdirs() && !target.getParentFile().isDirectory()) {
            throw new RuntimeException("Could not create directory " + target.getParentFile());
        }
        if (target.isFile() && !target.canWrite()) {
            throw new RuntimeException("Do not have write permissions for " + target + " - aborting!");
        }
    }
    
    public static String getDigest(File file, String algorithm, int hashLength) {
        DigestInputStream stream = null;
        try {
            stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
            byte[] buffer = new byte[65536];
            int read;
            do {
                read = stream.read(buffer);
            } while (read > 0);
        }
        catch (Exception ignored) {
            return null;
        }
        finally {
            closeSilently(stream);
        }
        closeSilently(stream);
        return String.format("%1$0" + hashLength + "x", new BigInteger(1, stream.getMessageDigest().digest()));
    }
    
    public File getDestination() {
        if (this.getTarget() == null) {
            return this.destination;
        }
        return this.getTarget();
    }
}
