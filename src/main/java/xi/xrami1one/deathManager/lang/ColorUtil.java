package xi.xrami1one.deathManager.lang;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String format(String msg) {
        String version = Bukkit.getVersion();
        if (version.contains("1.16") || version.contains("1.17") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20") || version.contains("1.21")) {
            Matcher matcher = pattern.matcher(msg);
            while (matcher.find()) {
                String color = msg.substring(matcher.start(), matcher.end());
                msg = msg.replace(color, "" + ChatColor.valueOf(color));
                matcher = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
