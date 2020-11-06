package net.ravage.update.minecraft.utils;

import java.io.FileNotFoundException;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.io.InputStream;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.io.FileInputStream;
import java.io.File;

public class FileUtils
{
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    
    public static String removeExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        if (!getFileExtension(new File(fileName)).isEmpty()) {
            return fileName.substring(0, fileName.lastIndexOf(46));
        }
        return fileName;
    }
    
    public static String getSHA1(File file) {
        try {
            InputStream input = new FileInputStream(file);
            try {
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                byte[] buffer = new byte[8192];
                for (int len = input.read(buffer); len != -1; len = input.read(buffer)) {
                    sha1.update(buffer, 0, len);
                }
                return new HexBinaryAdapter().marshal(sha1.digest()).toLowerCase();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    public static ArrayList<File> listFilesForFolder(File folder) {
        ArrayList<File> files = new ArrayList<File>();
        File[] listFiles;
        for (int length = (listFiles = folder.listFiles()).length, i = 0; i < length; ++i) {
            File fileEntry = listFiles[i];
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry));
                files.add(fileEntry);
            }
            else {
                files.add(fileEntry);
            }
        }
        return files;
    }
    
    public static void unzip(File zipfile, File folder) throws FileNotFoundException, IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipfile.getCanonicalFile())));
        ZipEntry ze = null;
        try {
            while ((ze = zis.getNextEntry()) != null) {
                File f = new File(folder.getCanonicalPath(), ze.getName());
                if (ze.isDirectory()) {
                    f.mkdirs();
                }
                else {
                    f.getParentFile().mkdirs();
                    OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
                    try {
                        try {
                            byte[] buf = new byte[8192];
                            int bytesRead;
                            while (-1 != (bytesRead = zis.read(buf))) {
                                fos.write(buf, 0, bytesRead);
                            }
                        }
                        finally {
                            fos.close();
                        }
                        fos.close();
                    }
                    catch (IOException ioe) {
                        f.delete();
                        throw ioe;
                    }
                }
            }
        }
        finally {
            zis.close();
        }
        zis.close();
    }
    
    public static void removeFolder(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            ArrayList<File> files = listFilesForFolder(folder);
            if (files.isEmpty()) {
                folder.delete();
                return;
            }
            for (File f : files) {
                f.delete();
            }
            folder.delete();
        }
    }
    
    public static ArrayList<File> listRecursive(File directory) {
        ArrayList<File> files = new ArrayList<File>();
        File[] fs = directory.listFiles();
        if (fs == null) {
            return files;
        }
        for (File f : fs) {
            if (f.isDirectory()) {
                files.addAll(listRecursive(f));
            }
            files.add(f);
        }
        return files;
    }
    
    public static File get(File root, String file) {
        File f = new File(root, file);
        return f;
    }
    
    public static File dir(File d) {
        return d;
    }
    
    public static File dir(File root, String dir) {
        return dir(get(root, dir));
    }
    
    public static File[] list(File dir) {
        File[] files = dir(dir).listFiles();
        return (files == null) ? new File[0] : files;
    }
}
