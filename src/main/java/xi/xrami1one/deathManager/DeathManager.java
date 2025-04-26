package xi.xrami1one.deathManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xi.xrami1one.deathManager.items.LightStone;
import xi.xrami1one.deathManager.items.MortalTotem;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.other.PluginParam;

import java.util.*;

public class DeathManager {
    private static HashMap<UUID, List<ItemStack>> graveMap = DeathPlugin.getPlugin().loadGraveMap();
    private static HashMap<UUID, List<Location>> graveLocationMap = DeathPlugin.getPlugin().loadGraveLocation();
    private static HashMap<Player, GameMode> spiritPlayers = new HashMap<>();
    private static HashMap<Player, Integer> playerLifesMap = DeathPlugin.getPlugin().loadPlayerLifesMap();
    private static List<UUID> deathTeleportPlayers = new ArrayList<>();

    private LangManager lm = new LangManager();

    public boolean deathPlayer(Player player, PlayerDeathEvent event) {
        World world = player.getWorld();
        event.setDeathMessage(null);
        if (DeathPlugin.getPlugin().getDeathWorld() != null) {
            if (DeathPlugin.getPlugin().getDeathWorld().equals(world)) {
                event.setDeathSound(null);
                event.setCancelled(true);
                return true;
            }
        }

        ItemStack totem = new MortalTotem().stack();
        if (wearItem(player, totem) && decreaseItem(player, totem)) {
            event.setCancelled(true);
            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getLocation().add(0, 3, 0), 5);
            for (Player p : player.getLocation().getNearbyEntitiesByType(Player.class, 5)) {
                if (!wearItem(p, totem)) {
                    p.damage(p.getMaxHealth());
                }
            }
            return true;
        }

        if (wearItem(player, new LightStone().stack(player))) {
            world.strikeLightning(player.getLocation());
        }

        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (PluginParam.sendDeathMessage) {
            String causeDescription = null;
            if (damageEvent != null) {
                causeDescription = getCauseMap().getOrDefault(damageEvent.getCause(), lm.msg("cause-map.unknown"));
            } else {
                causeDescription = getCauseMap().getOrDefault(null, lm.msg("cause-map.unknown"));
            }
            String deathMessage = lm.msg("death-message.p")
                    .replace("%pname%", player.getName())
                    .replace("%cause%", causeDescription);
            event.setDeathMessage(deathMessage);
        }
        event.setDeathSound(null);
        if (PluginParam.playSound) {
            event.setDeathSound(playDeathSound());
        }
        if (PluginParam.showParticles) {
            spawnDeathParticles(player);
        }
        if (PluginParam.grave) {
            List<ItemStack> loot = Arrays.asList(player.getInventory().getContents());
            event.getDrops().clear();

            Location deathLocation = player.getLocation();
            ArmorStand armorStand = player.getWorld().spawn(deathLocation.clone().add(0, -0.75, 0), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(true);
            armorStand.setCustomNameVisible(true);
            armorStand.getPersistentDataContainer().set(PluginParam.GraveKey, PersistentDataType.STRING, player.getUniqueId().toString());
            armorStand.setCustomName(lm.msg("m.grave-name").formatted(player.getName()));

            if (graveMap.containsKey(player.getUniqueId())) {
                List<ItemStack> beforeLoot = graveMap.get(player.getUniqueId());
                beforeLoot.addAll(loot);
                graveMap.remove(player.getUniqueId());
                graveMap.put(player.getUniqueId(), beforeLoot);
            } else {
                graveMap.put(player.getUniqueId(), loot);
            }
            List<Location> locals = new ArrayList<>();
            if (graveLocationMap.containsKey(player.getUniqueId())) {
                locals = graveLocationMap.get(player.getUniqueId());
            }
            locals.add(deathLocation);

            graveLocationMap.put(player.getUniqueId(), locals);
            return true;
        }
        if (PluginParam.spiritMode) {
            if (getSpiritPlayers().containsKey(player)) {
                event.setCancelled(true);
            }
            GameMode beforeMode = player.getGameMode();
            spiritPlayers.put(player, beforeMode);
            return true;
        }
        if (PluginParam.limitedLife) {
            if (!playerLifesMap.containsKey(player)) {
                playerLifesMap.put(player, PluginParam.amountLimitedLife);
            }
            int nowLifes = playerLifesMap.get(player);
            nowLifes--;
            if (nowLifes > 0) {
                playerLifesMap.remove(player);
                playerLifesMap.put(player, nowLifes);
                player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lm.msg("m.lastLifes").formatted(nowLifes)));
            } else {
                deathTeleportPlayers.add(player.getUniqueId());
            }
            return true;
        }
        return true;
    }

    private Sound playDeathSound() {
        String soundName = PluginParam.deathSound;
        try {
            Sound sound = Sound.valueOf(soundName);
            return sound;
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Sound not found: " + soundName);
        }
        return null;
    }

    private void spawnDeathParticles(Player player) {
        String particleName = PluginParam.particleEffect;
        int amount = PluginParam.particleAmount;
        double size = PluginParam.particleSize;

        try {
            Particle particle = Particle.valueOf(particleName);
            player.getWorld().spawnParticle(particle, player.getLocation(), amount, size, size, size, 0);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Particle not found: " + particleName);
        }
    }

    private boolean wearItem(Player player, ItemStack item) {
        for (ItemStack is : player.getInventory()) {
            if (is != null) {
                if (is.getType() != Material.AIR) {
                    if (is.hasItemMeta()) {
                        if (is.getItemMeta().hasCustomModelData()) {
                            int data = item.getItemMeta().getCustomModelData();
                            if (data == item.getItemMeta().getCustomModelData()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Map<EntityDamageEvent.DamageCause, String> getCauseMap() {
        Map<EntityDamageEvent.DamageCause, String> causeMap = new HashMap<>();
        for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
            causeMap.put(cause, lm.msg("cause-map.%s".formatted(cause.name())));
        }
        return causeMap;
    }

    public boolean decreaseItem(Player player, ItemStack item) {
        if (player.getInventory().containsAtLeast(item, 1)) {
            int slot = player.getInventory().first(item.getType());
            ItemStack inventoryItem = player.getInventory().getItem(slot);

            if (inventoryItem != null) {
                inventoryItem.setAmount(inventoryItem.getAmount() - 1);
                if (inventoryItem.getAmount() <= 0) {
                    player.getInventory().setItem(slot, null);
                }
                return true;
            }
        }
        return false;
    }

    public static HashMap<UUID, List<ItemStack>> getGraveMap() {
        return graveMap;
    }

    public static HashMap<UUID, List<Location>> getGraveLocationMap() {
        return graveLocationMap;
    }

    public static HashMap<Player, GameMode> getSpiritPlayers() {
        return spiritPlayers;
    }

    public static HashMap<Player, Integer> getPlayerLifesMap() {
        return playerLifesMap;
    }

    public static List<UUID> getDeathTeleportPlayers() {
        return deathTeleportPlayers;
    }
}

