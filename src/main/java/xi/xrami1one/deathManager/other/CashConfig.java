package xi.xrami1one.deathManager.other;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xi.xrami1one.deathManager.DeathPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CashConfig {
    private DeathPlugin instance = DeathPlugin.getPlugin();

    private File folder = this.instance.getServer().getPluginManager().getPlugin(this.instance.getName()).getDataFolder();

    private FileConfiguration cashYml = (FileConfiguration) YamlConfiguration.loadConfiguration(getFather());

    public File getFather() {
        List<File> files = Arrays.asList(this.folder.listFiles());
        for (File Ffile : files) {
            if (Ffile.getName().equals("cash.yml"))
                return Ffile;
        }
        return null;
    }
}
