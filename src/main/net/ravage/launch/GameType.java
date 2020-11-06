package net.ravage.launch;

import java.io.File;
import java.util.ArrayList;
import net.ravage.BaseLauncher;
import net.ravage.Main;

public abstract class GameType
{
    public static GameType V1_7_2_LOWER;
    public static GameType V1_7_10;
    public static GameType V1_8_HIGHER;
    public static GameType V1_13_HIGHER_FORGE;
    
    public static GameType get(String version) {
        String str = version.substring(2);
        Double d = Double.parseDouble(str);
        if (d <= 7.2) {
            return GameType.V1_7_2_LOWER;
        }
        if (d == 7.1) {
            return GameType.V1_7_10;
        }
        if (d >= 1.8) {
            return GameType.V1_8_HIGHER;
        }
        return GameType.V1_13_HIGHER_FORGE;
    }
    
    public abstract String getName();
    
    public abstract ArrayList<String> getLaunchArgs(BaseLauncher p0);
    
    public abstract String getMainClass();
    
    static {
        V1_7_2_LOWER = new GameType() {
            @Override
            public String getName() {
                return "1.7.2 or lower";
            }
            
            @Override
            public String getMainClass() {
                return "net.minecraft.client.main.Main";
            }
            
            @Override
            public ArrayList<String> getLaunchArgs(BaseLauncher launcher) {
                ArrayList<String> arguments = new ArrayList<String>();
                arguments.add("--username=" + Main.account.getDisplayName());
                arguments.add("--accessToken");
                arguments.add(Main.account.getAccessToken());
                arguments.add("--version");
                arguments.add(launcher.getMinecraftVersion());
                arguments.add("--gameDir");
                arguments.add(launcher.MINECRAFT_DIR.getAbsolutePath());
                arguments.add("--assetsDir");
                File assetsDir = launcher.getMinecraftFolder().getAssetsFolder();
                arguments.add(assetsDir.getAbsolutePath() + "/virtual/legacy/");
                arguments.add("--userProperties");
                arguments.add("{}");
                arguments.add("--uuid");
                arguments.add(Main.account.getUUID());
                arguments.add("--userType");
                arguments.add("legacy");
                return arguments;
            }
        };
        V1_7_10 = new GameType() {
            @Override
            public String getName() {
                return "1.7.10";
            }
            
            @Override
            public String getMainClass() {
                return "net.minecraft.client.main.Main";
            }
            
            @Override
            public ArrayList<String> getLaunchArgs(BaseLauncher launcher) {
                ArrayList<String> arguments = new ArrayList<String>();
                arguments.add("--username=" + Main.account.getDisplayName());
                arguments.add("--accessToken");
                arguments.add(Main.account.getAccessToken());
                arguments.add("--version");
                arguments.add(launcher.getMinecraftVersion());
                arguments.add("--gameDir");
                arguments.add(launcher.MINECRAFT_DIR.getAbsolutePath());
                arguments.add("--assetsDir");
                File assetsDir = launcher.getMinecraftFolder().getAssetsFolder();
                arguments.add(assetsDir.getAbsolutePath());
                arguments.add("--assetIndex");
                arguments.add(launcher.getMinecraftVersion());
                arguments.add("--userProperties");
                arguments.add("{}");
                arguments.add("--uuid");
                arguments.add(Main.account.getUUID());
                arguments.add("--userType");
                arguments.add("legacy");
                return arguments;
            }
        };
        V1_8_HIGHER = new GameType() {
            @Override
            public String getName() {
                return "1.8 or higher";
            }
            
            @Override
            public String getMainClass() {
                return "net.minecraft.client.main.Main";
            }
            
            @Override
            public ArrayList<String> getLaunchArgs(BaseLauncher launcher) {
                ArrayList<String> arguments = new ArrayList<String>();
                arguments.add("--username=" + Main.account.getDisplayName());
                arguments.add("--accessToken");
                arguments.add(Main.account.getAccessToken());
                arguments.add("--version");
                arguments.add(launcher.getMinecraftVersion());
                arguments.add("--gameDir");
                arguments.add(launcher.MINECRAFT_DIR.getAbsolutePath());
                arguments.add("--assetsDir");
                File assetsDir = launcher.getMinecraftFolder().getAssetsFolder();
                arguments.add(assetsDir.getAbsolutePath());
                arguments.add("--assetIndex");
                String version = launcher.getMinecraftVersion();
                int first = version.indexOf(46);
                int second = version.lastIndexOf(46);
                if (first != second) {
                    version = version.substring(0, version.lastIndexOf(46));
                }
                if (launcher.getMinecraftVersion().equals("1.13.1") || launcher.getMinecraftVersion().equals("1.13.2")) {
                    version = "1.13.1";
                }
                arguments.add(version);
                arguments.add("--userProperties");
                arguments.add("{}");
                arguments.add("--uuid");
                arguments.add(Main.account.getUUID());
                arguments.add("--userType");
                arguments.add("legacy");
                return arguments;
            }
        };
        V1_13_HIGHER_FORGE = new GameType() {
            @Override
            public String getName() {
                return "1.13 or higher with Forge";
            }
            
            @Override
            public String getMainClass() {
                return "cpw.mods.modlauncher.Launcher";
            }
            
            @Override
            public ArrayList<String> getLaunchArgs(BaseLauncher launcher) {
                ArrayList<String> arguments = new ArrayList<String>();
                arguments.add("--username=" +Main.account.getDisplayName());
                arguments.add("--accessToken");
                arguments.add(Main.account.getAccessToken());
                arguments.add("--version");
                arguments.add(launcher.getMinecraftVersion());
                arguments.add("--gameDir");
                arguments.add(launcher.MINECRAFT_DIR.getAbsolutePath());
                arguments.add("--assetsDir");
                File assetsDir = launcher.getMinecraftFolder().getAssetsFolder();
                arguments.add(assetsDir.getAbsolutePath());
                arguments.add("--assetIndex");
                arguments.add("1.13.1");
                arguments.add("--userProperties");
                arguments.add("{}");
                arguments.add("--uuid");
                arguments.add(Main.account.getUUID());
                arguments.add("--userType");
                arguments.add("legacy");
                arguments.add("--launchTarget");
                arguments.add("fmlclient");
                arguments.add("--fml.forgeVersion");
                arguments.add("25.0.219");
                arguments.add("--fml.mcVersion");
                arguments.add("1.13.2");
                arguments.add("--fml.forgeGroup");
                arguments.add("net.minecraftforge");
                arguments.add("--fml.mcpVersion");
                arguments.add("20190213.203750");
                return arguments;
            }
        };
    }
}
