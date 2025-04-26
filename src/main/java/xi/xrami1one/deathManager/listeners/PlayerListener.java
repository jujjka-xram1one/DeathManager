package xi.xrami1one.deathManager.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import xi.xrami1one.deathManager.DeathManager;
import xi.xrami1one.deathManager.DeathPlugin;
import xi.xrami1one.deathManager.items.GraveKey;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.other.PluginParam;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DeathPlugin.getPlugin().getManager().deathPlayer(player, event);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        LangManager lm = new LangManager();
        if(DeathManager.getDeathTeleportPlayers().contains(player.getUniqueId())){
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setInvulnerable(true);
            EntityListener.getWinPlayers().remove(player);
            DeathManager.getDeathTeleportPlayers().remove(player.getUniqueId());
            player.sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(lm.msg("m.deathTeleport")));
            event.setRespawnLocation(DeathPlugin.getPlugin().getDeathWorld().getSpawnLocation());
            return;
        }

        if(DeathManager.getGraveMap().containsKey(player.getUniqueId())){
            for (ItemStack is : player.getInventory()){
                if(is != null){
                    if(is.getType() != Material.AIR){
                        player.getInventory().remove(is);
                    }
                }
            }
            ItemStack key = new GraveKey().stack(player);
            player.getInventory().addItem(key);
        }

        if(DeathManager.getSpiritPlayers().containsKey(player)){
            player.setGameMode(GameMode.SPECTATOR);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.setGameMode(DeathManager.getSpiritPlayers().get(player));
                    DeathManager.getSpiritPlayers().remove(player);
                }
            }.runTaskLater(DeathPlugin.getPlugin(),PluginParam.spiritModeTime * 20);
            return;
        }
    }

    @EventHandler
    public void clickToGrave(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();

        if(!(event.getRightClicked() instanceof ArmorStand stand)){
            return;
        }
        if(event.getPlayer().getItemInHand() == null){
            return;
        }
        ItemStack itemInHand = player.getItemInHand();
        if (!itemInHand.hasItemMeta()) {
            return;
        }
        ItemMeta meta = itemInHand.getItemMeta();
        if (meta.hasCustomModelData() && meta.getCustomModelData() != 33836534) {
            return;
        }
        if (!stand.getPersistentDataContainer().has(PluginParam.GraveKey, PersistentDataType.STRING)){
            return;
        }
        UUID uuid = player.getUniqueId();
        if(!DeathManager.getGraveMap().containsKey(uuid)){
            return;
        }
        player.getInventory().remove(itemInHand);
        List<ItemStack> loot = DeathManager.getGraveMap().get(uuid);
        if (loot != null && !loot.isEmpty()) {
            for (ItemStack item : loot) {
                if (item != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
        stand.remove();
        DeathManager.getGraveMap().remove(uuid);
        List<Location> locals = DeathManager.getGraveLocationMap().get(uuid);
        locals.remove(getNearbyLocation(uuid,event.getRightClicked().getLocation()));

        DeathManager.getGraveLocationMap().remove(uuid);
        if(!locals.isEmpty()){
            DeathManager.getGraveLocationMap().put(uuid,locals);
        }
    }

    private Location getNearbyLocation(UUID uuid,Location clickedLocation){
        Location location = null;

        for (Location l : DeathManager.getGraveLocationMap().get(uuid)){
            if(l.distance(clickedLocation) <= 1){
                location = l;
            }
        }
        return location;
    }
}
