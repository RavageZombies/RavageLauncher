package net.ravage.util;

import java.lang.reflect.Field;
import java.io.File;
import net.ravage.BaseLauncher;

public class JavaUtil
{
    public static boolean hasJava64() {
        return System.getProperty("sun.arch.data.model").equalsIgnoreCase("64");
    }
    
    public static String[] getSpecialArgs() {
        return new String[] { "-XX:-UseAdaptiveSizePolicy", "-XX:+UseConcMarkSweepGC" };
    }
    
    public static String macDockName(String name) {
        return "-Xdock:name=" + name;
    }
    
    public static String getJavaCommand(BaseLauncher launcher) {
        String dir = System.getProperty("java.home");
        if (!hasJava64() && launcher.getUpdateManager().hasCustomJre()) {
            dir = launcher.CUSTOM_JRE_DIR.getAbsolutePath();
        }
        return dir + File.separator + "bin" + File.separator + "java";
    }
    
    public static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }
}
