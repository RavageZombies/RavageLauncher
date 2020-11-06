package net.ravage.launch;

import java.io.IOException;
import java.util.Arrays;
import net.ravage.util.JavaUtil;
import net.ravage.util.MinecraftFolder;
import java.util.ArrayList;
import net.ravage.util.FileList;
import net.ravage.update.minecraft.utils.FileUtils;
import net.ravage.util.RavageUtils;
import java.io.File;
import java.util.List;
import net.ravage.BaseLauncher;

public class MinecraftLauncher
{
    private BaseLauncher launcher;
    private String mainClass;
    private String classPath;
    private List<String> vmArgs;
    private List<String> args;
    private boolean redirectErrorStream;
    private String macDockName;
    private File directory;
    
    public MinecraftLauncher(BaseLauncher launcher) {
        this.redirectErrorStream = true;
        this.launcher = launcher;
        MinecraftFolder minecraftFolder = launcher.getMinecraftFolder();
        RavageUtils.sendMessageInConsole("Cr\u00e9ation d'un profil de lancement pour " + launcher.getMinecraftVersion());
        if (launcher.getForgeVersion() != null) {
            RavageUtils.sendMessageInConsole("Version de forge d\u00e9finie: " + launcher.getForgeVersion());
        }
        checkFolder(minecraftFolder);
        ClasspathConstructor constructor = new ClasspathConstructor();
        constructor.add(new FileList(FileUtils.listRecursive(minecraftFolder.getLibrariesFolder())).files().match("^(.*\\.((jar)$))*$").get());
        constructor.add(minecraftFolder.getClientJarFile());
        String mainClass = (launcher.getForgeVersion() == null) ? GameType.get(launcher.getMinecraftVersion()).getMainClass() : "net.minecraft.launchwrapper.Launch";
        String classpath = constructor.make();
        List<String> args = launcher.getUpdateManager().getVersionObj().getGameType().getLaunchArgs(launcher);
        List<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Djava.library.path=" + minecraftFolder.getNativesFolder().getAbsolutePath());
        vmArgs.add("-Dfml.ignoreInvalidMinecraftCertificates=true");
        vmArgs.add("-Dfml.ignorePatchDiscrepancies=true");
        if (launcher.getForgeVersion() != null) {
            args.add("--tweakClass");
            if (GameType.get(launcher.getMinecraftVersion()).equals(GameType.V1_8_HIGHER)) {
                args.add("net.minecraftforge.fml.common.launcher.FMLTweaker");
            }
            else {
                args.add("cpw.mods.fml.common.launcher.FMLTweaker");
            }
        }
        this.mainClass = mainClass;
        this.classPath = classpath;
        this.vmArgs = vmArgs;
        this.args = args;
        this.redirectErrorStream = true;
        this.macDockName = launcher.getLauncherName();
        this.directory = launcher.MINECRAFT_DIR;
        RavageUtils.sendMessageInConsole("Cr\u00e9ation du profil termin\u00e9e");
    }
    
    public BaseLauncher getLauncher() {
        return this.launcher;
    }
    
    public String getMainClass() {
        return this.mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getClassPath() {
        return this.classPath;
    }
    
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
    
    public List<String> getVmArgs() {
        return this.vmArgs;
    }
    
    public void setVmArgs(List<String> vmArgs) {
        this.vmArgs = vmArgs;
    }
    
    public List<String> getArgs() {
        return this.args;
    }
    
    public void setArgs(List<String> args) {
        this.args = args;
    }
    
    public boolean isRedirectErrorStream() {
        return this.redirectErrorStream;
    }
    
    public void setRedirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
    }
    
    public String getMacDockName() {
        return this.macDockName;
    }
    
    public void setMacDockName(String macDockName) {
        this.macDockName = macDockName;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(File directory) {
        this.directory = directory;
    }
    
    public static void checkFolder(MinecraftFolder folder) {
        File assetsFolder = folder.getAssetsFolder();
        File libsFolder = folder.getLibrariesFolder();
        File nativesFolder = folder.getNativesFolder();
        File minecraftJar = folder.getClientJarFile();
        if (!assetsFolder.exists() || assetsFolder.listFiles() == null) {
            RavageUtils.crash("Missing/Empty assets folder !", "(" + assetsFolder.getAbsolutePath() + ")");
        }
        else if (!libsFolder.exists() || libsFolder.listFiles() == null) {
            RavageUtils.crash("Missing/Empty libraries folder !", "(" + libsFolder.getAbsolutePath() + ")");
        }
        else if (!nativesFolder.exists() || nativesFolder.listFiles() == null) {
            RavageUtils.crash("Missing/Empty natives folder !", "(" + nativesFolder.getAbsolutePath() + ")");
        }
        else if (!minecraftJar.exists()) {
            RavageUtils.crash("Missing main jar !", "(" + minecraftJar.getAbsolutePath() + ")");
        }
    }
    
    public Process launch() throws LaunchException {
        RavageUtils.sendMessageInConsole("Pr\u00e9paration au lancement de Minecraft...", false);
        ProcessBuilder builder = new ProcessBuilder(new String[0]);
        ArrayList<String> commands = new ArrayList<String>();
        commands.add(JavaUtil.getJavaCommand(this.getLauncher()));
        commands.addAll(Arrays.<String>asList(JavaUtil.getSpecialArgs()));
        if (this.getVmArgs() != null) {
            commands.addAll(this.getVmArgs());
        }
        commands.add("-cp");
        commands.add(this.getClassPath());
        commands.add(this.getMainClass());
        if (this.getArgs() != null) {
            commands.addAll(this.getArgs());
        }
        if (this.getDirectory() != null) {
            builder.directory(this.getDirectory());
        }
        if (this.isRedirectErrorStream()) {
            builder.redirectErrorStream(true);
        }
        builder.command(commands);
        String entireCommand = "";
        for (String command : commands) {
            entireCommand = entireCommand + command + " ";
        }
        RavageUtils.sendMessageInConsole("Commande de lancement: " + entireCommand, false);
        RavageUtils.sendMessageInConsole("Lancement de: " + this.getMainClass(), false);
        try {
            return builder.start();
        }
        catch (IOException e) {
            throw new LaunchException("Cannot launch !", e);
        }
    }
}
