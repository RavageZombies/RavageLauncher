package net.ravage.launch;

import java.util.Arrays;

import net.ravage.AppProperties;
import net.ravage.BaseLauncher;
import net.ravage.Main;

public class LaunchManager
{
    private BaseLauncher launcher;
    private String serverIp;
    
    public LaunchManager(final BaseLauncher launcher) {
        this.serverIp = null;
        this.launcher = launcher;
    }
    
    public void enableAutoServerConnect(final String serverIp) {
        this.serverIp = serverIp;
    }
    
    public void disabledAutoServerConnect() {
        this.serverIp = null;
    }
    
    public void launch() {
        final MinecraftLauncher launcher = new MinecraftLauncher(this.launcher);
        if (this.serverIp != null) {
            final String[] parts = this.serverIp.split(":");
            launcher.getArgs().add("--server=" + parts[0]);
            if (parts.length > 1) {
                launcher.getArgs().add("--port=" + parts[1]);
            }
            else {
                launcher.getArgs().add("--port=25565");
            }
        }
        final int minRam = 512;
        String ramValue = AppProperties.properties.getProperty("ram", "2");
        launcher.getVmArgs().addAll(Arrays.<String>asList("-Xms512M", "-Xmx" + (int)(Integer.parseInt(ramValue) * 1000.0) + "M"));
        try {
            launcher.launch();
        }
        catch (LaunchException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
