package xi.xrami1one.deathManager.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xi.xrami1one.deathManager.lang.LangManager;

public class MortalTotem {

    public ItemStack stack() {
        LangManager lm = new LangManager();
        ItemStack item = new ItemStack(Material.COAL);
        ItemMeta meta = item.getItemMeta();

        if(lm.msg("items.mortal-totem.name") != null) {
            meta.setDisplayName(lm.msg("items.mortal-totem.name"));
        }
        if(lm.msg_lore("items.mortal-totem.lore") != null) {
            meta.setLore(lm.msg_lore("items.mortal-totem.lore"));
        }

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_DESTROYS
        );

        meta.setCustomModelData(33836535);
        item.setItemMeta(meta);
        return item;
    }
}
