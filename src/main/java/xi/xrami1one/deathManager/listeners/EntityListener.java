package xi.xrami1one.deathManager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import xi.xrami1one.deathManager.DeathManager;
import xi.xrami1one.deathManager.DeathPlugin;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.other.PluginParam;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Sound.*;

public class EntityListener implements Listener {

    private static List<Player> winPlayers = new ArrayList<>();

    @EventHandler
    public void death(EntityDeathEvent event){
        World world = event.getEntity().getWorld();
        if(DeathPlugin.getPlugin().getDeathWorld()==null){
            return;
        }
        if(!DeathPlugin.getPlugin().getDeathWorld().equals(world)){
            return;
        }
        if(!event.getEntity().getPersistentDataContainer().has(PluginParam.bossKey, PersistentDataType.STRING)){
            return;
        }
        LangManager lm = new LangManager();
        for (Player p : world.getPlayers()){
            winPlayers.add(p);
            p.sendTitle(lm.msg("m.win"),lm.msg("m.subwin"));
            p.playSound(p.getLocation(),ENTITY_FIREWORK_ROCKET_BLAST,10,10);

            new BukkitRunnable(){
                @Override
                public void run() {
                    p.teleport(Bukkit.getWorld(PluginParam.defaultWorld).getSpawnLocation());
                    p.setInvulnerable(false);
                    DeathManager.getPlayerLifesMap().remove(p);
                    DeathManager.getPlayerLifesMap().put(p,PluginParam.amountLimitedLife);
                }
            }.runTaskLater(DeathPlugin.getPlugin(),100);
        }
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event){
        World world = event.getFrom().getWorld();
        if(DeathPlugin.getPlugin().getDeathWorld() ==null){
            return;
        }
        if(!DeathPlugin.getPlugin().getDeathWorld().equals(world)){
            return;
        }
        if(!winPlayers.contains(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    public static List<Player> getWinPlayers() {
        return winPlayers;
    }
}