package xi.xrami1one.deathManager.lang;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import xi.xrami1one.deathManager.DeathPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class LangManager {
    private DeathPlugin instance = DeathPlugin.getPlugin();
    private File folder = this.instance.getServer().getPluginManager().getPlugin(this.instance.getName()).getDataFolder();

    public void setupFiles() {
        File langFolder = new File(this.folder, "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        String[] languages = {"ru.yml","en.yml","de.yml","fr.yml","jp.yml"};
        for (String langFile : languages) {
            if (this.instance.getResource("lang/" + langFile) != null) {
                File targetFile = new File(langFolder, langFile);
                if (!targetFile.exists()) {
                    this.instance.saveResource("lang/" + langFile, false);
                }
            } else {
                Bukkit.getLogger().warning("Resource lang/" + langFile + " not found in JAR!");
            }
        }
    }

    public String msg(String path) {
        try {
            File config = getFather();
            return ColorUtil.format(YamlConfiguration.loadConfiguration(config).getString(path));
        } catch (Exception e){
        }
        return null;
    }

    public List<String> msg_lore(String path) {
        File config = getFather();
        return YamlConfiguration.loadConfiguration(config).getStringList(path);
    }

    private File getFather() {
        List<File> files = Arrays.asList(this.folder.listFiles());
        for (File Ffile : files) {
            if (Ffile.getName().equals("lang")) {
                for (File file : Ffile.listFiles()) {
                    if (file.getName().equals(DeathPlugin.getPlugin().getConfig().getString("lang")))
                        return file;
                }
            }
        }
        return null;
    }
}
