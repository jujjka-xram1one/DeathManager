package xi.xrami1one.deathManager.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class DeathTable implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> table = new ArrayList<>();
        switch (args.length) {
            case 1:
                table.add("enable-death-message");
                table.add("enable-grave");
                table.add("enable-spiritMode");
                table.add("enable-limitedLife");
                table.add("getMortalTotem");
                table.add("checkGraves");
                break;
        }
        return table;
    }
}
