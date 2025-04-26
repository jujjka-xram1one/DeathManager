package xi.xrami1one.deathManager.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xi.xrami1one.deathManager.lang.LangManager;

public class GraveKey {

    public ItemStack stack(Player player) {
        LangManager lm = new LangManager();
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();

        if(lm.msg("items.grave-key.name") != null) {
            meta.setDisplayName(lm.msg("items.grave-key.name"));
        }
        if(lm.msg_lore("items.grave-key.lore") != null) {
            meta.setLore(lm.msg_lore("items.grave-key.lore"));
        }
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_DESTROYS
        );
        meta.setCustomModelData(33836534);
        item.setItemMeta(meta);
        return item;
    }
}
