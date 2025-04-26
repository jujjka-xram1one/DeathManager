package xi.xrami1one.deathManager.other;

import org.bukkit.NamespacedKey;
import xi.xrami1one.deathManager.DeathPlugin;

public class PluginParam {
    public static NamespacedKey GraveKey = new NamespacedKey(DeathPlugin.getPlugin(), "uuid");
    public static NamespacedKey bossKey = new NamespacedKey(DeathPlugin.getPlugin(),"boss");

    public static boolean sendDeathMessage = DeathPlugin.getConfiguration().getBoolean("events-settings.playerDeath.send-death-message");
    public static boolean playSound = DeathPlugin.getConfiguration().getBoolean("events-settings.playerDeath.sound.enabled");
    public static String deathSound = DeathPlugin.getConfiguration().getString("events-settings.playerDeath.sound.name", "ENTITY_PLAYER_DEATH");
    public static boolean showParticles = DeathPlugin.getConfiguration().getBoolean("events-settings.playerDeath.animation.particle.enabled");
    public static String particleEffect = DeathPlugin.getConfiguration().getString("events-settings.playerDeath.animation.particle.name", "EXPLOSION_NORMAL");
    public static double particleSize = DeathPlugin.getConfiguration().getDouble("events-settings.playerDeath.animation.particle.size", 0.5);
    public static int particleAmount = DeathPlugin.getConfiguration().getInt("events-settings.playerDeath.animation.particle.amount", 50);

    public static boolean spiritMode = DeathPlugin.getConfiguration().getBoolean("spirit_mode", false);
    public static int spiritModeTime = DeathPlugin.getConfiguration().getInt("spirit_mode_time", 600);
    public static boolean limitedLife = DeathPlugin.getConfiguration().getBoolean("limited_life", false);
    public static int amountLimitedLife = DeathPlugin.getConfiguration().getInt("amount_limited_life", 5);
    public static String defaultWorld = DeathPlugin.getConfiguration().getString("default-world", "world");
    public static boolean grave = DeathPlugin.getConfiguration().getBoolean("grave", true);
}
