package net.ravage.update.minecraft.utils;

import java.io.IOException;
import java.net.URI;
import java.io.File;

public enum OperatingSystem
{
    LINUX("LINUX", 0, "linux", new String[] { "linux", "unix" }), 
    WINDOWS("WINDOWS", 1, "windows", new String[] { "win" }), 
    OSX("OSX", 2, "osx", new String[] { "mac" }), 
    UNKNOWN("UNKNOWN", 3, "unknown", new String[0]);
    
    private String name;
    private String[] aliases;
    
    private OperatingSystem(String s, int n, String name, String[] aliases) {
        this.name = name;
        this.aliases = ((aliases == null) ? new String[0] : aliases);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
    
    public boolean isSupported() {
        return this != OperatingSystem.UNKNOWN;
    }
    
    public String getJavaDir() {
        String separator = System.getProperty("file.separator");
        String path = String.valueOf(System.getProperty("java.home")) + separator + "bin" + separator;
        if (getCurrentPlatform() == OperatingSystem.WINDOWS && new File(String.valueOf(path) + "javaw.exe").isFile()) {
            return String.valueOf(path) + "javaw.exe";
        }
        return String.valueOf(path) + "java";
    }
    
    public static OperatingSystem getCurrentPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        OperatingSystem[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            OperatingSystem os = values[i];
            String[] aliases;
            for (int length2 = (aliases = os.getAliases()).length, j = 0; j < length2; ++j) {
                String alias = aliases[j];
                if (osName.contains(alias)) {
                    return os;
                }
            }
        }
        return OperatingSystem.UNKNOWN;
    }
    
    public static void openLink(URI link) {
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object o = desktopClass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            desktopClass.getMethod("browse", URI.class).invoke(o, link);
        }
        catch (Throwable e2) {
            if (getCurrentPlatform() == OperatingSystem.OSX) {
                try {
                    Runtime.getRuntime().exec(new String[] { "/usr/bin/open", link.toString() });
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            else {
                e2.printStackTrace();
            }
        }
    }
    
    public static void openFolder(File path) {
        String absolutePath = path.getAbsolutePath();
        OperatingSystem os = getCurrentPlatform();
        if (os == OperatingSystem.OSX) {
            try {
                Runtime.getRuntime().exec(new String[] { "/usr/bin/open", absolutePath });
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        if (os == OperatingSystem.WINDOWS) {
            String cmd = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);
            try {
                Runtime.getRuntime().exec(cmd);
                return;
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object desktop = desktopClass.getMethod("getDesktop", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
            desktopClass.getMethod("browse", URI.class).invoke(desktop, path.toURI());
        }
        catch (Throwable e3) {
            e3.printStackTrace();
        }
    }
}
