package me.bughatti.ultimatetitles;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public final class Placeholders {

    private Placeholders() {
    }

    public static String apply(UltimateTitles plugin, Player player, String text, Map<String, String> extra) {
        if (text == null) {
            return "";
        }

        // Variables extra del evento primero (ej: %killer%, %victim%, %level%)
        if (extra != null) {
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                text = text.replace(entry.getKey(), entry.getValue());
            }
        }

        boolean papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        String rank = plugin.getConfig().getString("default-rank", "Default");
        String rankPlaceholder = plugin.getConfig().getString("rank-placeholder", "");
        if (papi && rankPlaceholder != null && !rankPlaceholder.isEmpty()) {
            String parsed = PlaceholderAPI.setPlaceholders(player, rankPlaceholder);
            if (!parsed.isEmpty() && !parsed.equals(rankPlaceholder)) {
                rank = parsed;
            }
        }

        text = text.replace("%user%", player.getName())
                .replace("%rank%", rank)
                .replace("%level%", String.valueOf(player.getLevel()))
                .replace("%kill%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS)))
                .replace("%death%", String.valueOf(player.getStatistic(Statistic.DEATHS)))
                .replace("%world%", player.getWorld().getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (papi) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
