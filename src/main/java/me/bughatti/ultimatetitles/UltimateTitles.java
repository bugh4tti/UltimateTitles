package me.bughatti.ultimatetitles;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UltimateTitles extends JavaPlugin {

    private TitleManager titleManager;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResourceIfMissing("titles.yml");
        saveResourceIfMissing("messages.yml");
        loadMessages();

        titleManager = new TitleManager(this);

        PluginCommand command = getCommand("ultimatetitles");
        if (command != null) {
            UTCommand executor = new UTCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new TitleListener(this), this);
        getLogger().info("UltimateTitles activado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("UltimateTitles desactivado.");
    }

    public void reloadAll() {
        reloadConfig();
        loadMessages();
        titleManager.reload();
    }

    private void saveResourceIfMissing(String name) {
        if (!new File(getDataFolder(), name).exists()) {
            saveResource(name, false);
        }
    }

    private void loadMessages() {
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    /** Mensaje sin prefijo. Los reemplazos van en pares: "%clave%", "valor". */
    public String raw(String key, String... replacements) {
        String text = messages.getString(key, key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return ColorUtil.color(text);
    }

    /** Mensaje con prefijo. */
    public String msg(String key, String... replacements) {
        return ColorUtil.color(messages.getString("prefix", "")) + raw(key, replacements);
    }

    public List<String> rawList(String key) {
        List<String> result = new ArrayList<>();
        for (String line : messages.getStringList(key)) {
            result.add(ColorUtil.color(line));
        }
        return result;
    }
                                                                        }
