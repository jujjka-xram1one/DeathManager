package xi.xrami1one.deathManager.mobs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.other.PluginParam;

public class DeathBoss {

    public LivingEntity getEntity(Location location){
        World world = location.getWorld();
        LivingEntity entity = (LivingEntity) world.spawnEntity(location, EntityType.WITHER);
        entity.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(0);
        entity.getPersistentDataContainer().set(PluginParam.bossKey, PersistentDataType.STRING,"boss");
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&',"&7&lGod of Death"));
        entity.setMaxHealth(200);
        entity.setHealth(200);

        for (Player p : world.getPlayers()){
            p.sendMessage(new LangManager().msg("m.bossSpawn"));
        }

        return entity;
    }
}
