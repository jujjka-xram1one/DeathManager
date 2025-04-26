package xi.xrami1one.deathManager.cmd;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xi.xrami1one.deathManager.DeathManager;
import xi.xrami1one.deathManager.DeathPlugin;
import xi.xrami1one.deathManager.items.MortalTotem;
import xi.xrami1one.deathManager.lang.LangManager;
import xi.xrami1one.deathManager.other.PluginParam;

public class DeathCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player) sender;
        if (args.length == 0)
            return false;
        LangManager lm = new LangManager();
        FileConfiguration config = DeathPlugin.getConfiguration();
        switch (args[0]) {
            case "getMortalTotem":
                player.getInventory().addItem(new MortalTotem().stack());
                player.sendMessage(lm.msg("m.get"));
                break;
            case "checkGraves":
                if (!DeathManager.getGraveMap().containsKey(player.getUniqueId())) {
                    player.sendMessage(lm.msg("m.notGraves"));
                    break;
                }
                int number = 0;
                for (Location l : DeathManager.getGraveLocationMap().get(player.getUniqueId())) {
                    number++;
                    player.sendMessage(lm.msg("m.graveCoordinates").formatted(
                            number,
                            l.getBlockX(),
                            l.getBlockY(),
                            l.getBlockZ()));
                }
                break;
        }

        return false;
    }
}
