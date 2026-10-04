package me.bughatti.ultimatetitles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TitleManager {

    private final UltimateTitles plugin;
    private File file;
    private FileConfiguration titles;

    public TitleManager(UltimateTitles plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        file = new File(plugin.getDataFolder(), "titles.yml");
        if (!file.exists()) {
            plugin.saveResource("titles.yml", false);
        }
        titles = YamlConfiguration.loadConfiguration(file);
    }

    public Set<String> getEvents() {
        return titles.getKeys(false);
    }

    public boolean exists(String id) {
        return titles.isConfigurationSection(id);
    }

    public boolean isEnabled(String id) {
        return titles.getBoolean(id + ".enabled", true);
    }

    public void setEnabled(String id, boolean enabled) {
        titles.set(id + ".enabled", enabled);
        try {
            titles.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar titles.yml: " + e.getMessage());
        }
    }

    /**
     * Envía el title de un evento a un jugador.
     * @return false si el evento no existe o está desactivado.
     */
    public boolean send(Player player, String id, Map<String, String> extra) {
        ConfigurationSection section = titles.getConfigurationSection(id);
        if (section == null || !section.getBoolean("enabled", true)) {
            return false;
        }

        String title = Placeholders.apply(plugin, player, section.getString("title", ""), extra);
        String subtitle = Placeholders.apply(plugin, player, section.getString("subtitle", ""), extra);

        int fadeIn = section.getInt("fade-in", 10);
        int stay = section.getInt("stay", 50);
        int fadeOut = section.getInt("fade-out", 10);

        player.sendTitle(ColorUtil.color(title), ColorUtil.color(subtitle), fadeIn, stay, fadeOut);

        String sound = section.getString("sound", "");
        if (sound != null && !sound.isEmpty()) {
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
        return true;
    }
}
