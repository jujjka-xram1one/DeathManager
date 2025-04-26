package xi.xrami1one.deathManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xi.xrami1one.deathManager.cmd.DeathCommands;
import xi.xrami1one.deathManager.cmd.DeathTable;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.listeners.EntityListener;
import xi.xrami1one.deathManager.listeners.PlayerListener;
import xi.xrami1one.deathManager.mobs.DeathBoss;
import xi.xrami1one.deathManager.other.CashConfig;
import xi.xrami1one.deathManager.other.FilterConsole;
import xi.xrami1one.deathManager.other.PluginParam;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class DeathPlugin extends JavaPlugin {

    private static DeathPlugin plugin;
    private static FileConfiguration config;

    private DeathManager manager;
    private World deathWorld;

    @Override
    public void onEnable() {
        ((Logger) LogManager.getRootLogger()).addFilter(new FilterConsole());
        plugin = this;
        saveDefaultConfig();
        config = getConfig();
        saveResource("cash.yml", false);
        manager = new DeathManager();
        new LangManager().setupFiles();

        createDeathWorld();
        registerListeners();
        registerCommands();

        new Metrics(this, 24235);
        startTimer();
        sendEnableMessage();

        logStartupMessage();
    }

    @Override
    public void onDisable() {
        saveAllData();
        restoreSpiritPlayers();
    }

    private void createDeathWorld() {
        WorldCreator creator = new WorldCreator("spirituality");
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.LARGE_BIOMES);
        creator.generateStructures(false);
        deathWorld = Bukkit.createWorld(creator);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    private void registerCommands() {
        getCommand("dm").setExecutor(new DeathCommands());
        getCommand("dm").setTabCompleter(new DeathTable());
    }

    private void sendEnableMessage() {
        if (getConfig().getBoolean("send-enable-plugin-message")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("enable-plugin-message")));
                }
            }
        }
    }

    private void logStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+---------------------------------------------+"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8| &7DeathManager &8- &fA dead world Awaits You!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&8| &fVersion: &7%s &8| &7Author: &fJujjka", getDescription().getVersion())));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+---------------------------------------------+"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7DeathManager &fhas &astarted successfully!"));
    }

    private void saveAllData() {
        saveGraveMap(DeathManager.getGraveMap());
        saveGraveLocationMap(DeathManager.getGraveLocationMap());
        savePlayerLifesMap(DeathManager.getPlayerLifesMap());
    }

    private void restoreSpiritPlayers() {
        for (Player p : DeathManager.getSpiritPlayers().keySet()) {
            p.setGameMode(DeathManager.getSpiritPlayers().get(p));
        }
    }

    public boolean startTimer() {
        if (deathWorld == null) {
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (LivingEntity entity : deathWorld.getLivingEntities()) {
                    if (entity.getType() == EntityType.WITHER && entity.getPersistentDataContainer().has(PluginParam.bossKey, PersistentDataType.STRING)) {
                        entity.remove();
                    }
                }
                new DeathBoss().getEntity(deathWorld.getSpawnLocation());
            }
        }.runTaskTimer(this, 15000, 15000);

        return true;
    }

    public void saveGraveLocationMap(HashMap<UUID, List<Location>> graveLocationMap) {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (UUID uuid : graveLocationMap.keySet()) {
            List<Location> locations = graveLocationMap.get(uuid);
            List<String> serializedLocations = new ArrayList<>();
            for (Location location : locations) {
                serializedLocations.add(serializeLocation(location));
            }
            config.set("graveLocations." + uuid.toString(), serializedLocations);
        }
        saveConfig(config, file);
    }

    public void saveGraveMap(HashMap<UUID, List<ItemStack>> graveMap) {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (UUID uuid : graveMap.keySet()) {
            List<ItemStack> items = graveMap.get(uuid);
            config.set("graveItems." + uuid.toString(), items);
        }
        saveConfig(config, file);
    }

    public void savePlayerLifesMap(HashMap<Player, Integer> playerLifesMap) {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Player player : playerLifesMap.keySet()) {
            config.set("playerLifes." + player.getUniqueId().toString(), playerLifesMap.get(player));
        }
        saveConfig(config, file);
    }

    public HashMap<UUID, List<ItemStack>> loadGraveMap() {
        return loadMap("graveItems");
    }

    public HashMap<UUID, List<Location>> loadGraveLocation() {
        return loadLocationMap("graveLocations");
    }

    public HashMap<Player, Integer> loadPlayerLifesMap() {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        HashMap<Player, Integer> playerLifesMap = new HashMap<>();
        if (config.contains("playerLifes")) {
            for (String uuidString : config.getConfigurationSection("playerLifes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    int lifes = config.getInt("playerLifes." + uuidString);
                    playerLifesMap.put(player, lifes);
                }
            }
        }
        return playerLifesMap;
    }

    private HashMap<UUID, List<ItemStack>> loadMap(String key) {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        HashMap<UUID, List<ItemStack>> map = new HashMap<>();
        if (config.contains(key)) {
            for (String uuidString : config.getConfigurationSection(key).getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                List<ItemStack> items = (List<ItemStack>) config.get(key + "." + uuidString);
                map.put(uuid, items);
            }
        }
        return map;
    }

    private HashMap<UUID, List<Location>> loadLocationMap(String key) {
        File file = new CashConfig().getFather();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        HashMap<UUID, List<Location>> map = new HashMap<>();
        if (config.contains(key)) {
            for (String uuidString : config.getConfigurationSection(key).getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                List<String> serializedLocations = config.getStringList(key + "." + uuidString);
                List<Location> locations = new ArrayList<>();
                for (String serializedLocation : serializedLocations) {
                    locations.add(deserializeLocation(serializedLocation));
                }
                map.put(uuid, locations);
            }
        }
        return map;
    }

    private void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serializeLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    private Location deserializeLocation(String serializedLocation) {
        String[] parts = serializedLocation.split(",");
        if (parts.length == 4) {
            return new Location(
                    Bukkit.getWorld(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3])
            );
        }
        return null;
    }

    public static DeathPlugin getPlugin() {
        return plugin;
    }

    public static FileConfiguration getConfiguration() {
        return config;
    }

    public DeathManager getManager() {
        return manager;
    }

    public World getDeathWorld() {
        return deathWorld;
    }
}
