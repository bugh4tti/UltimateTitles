package me.bughatti.ultimatetitles;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;

public class TitleListener implements Listener {

    private final UltimateTitles plugin;

    public TitleListener(UltimateTitles plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String id = player.hasPlayedBefore() ? "join" : "join-first";
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getTitleManager().send(player, id, null);
            }
        }, 10L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        Map<String, String> extra = new HashMap<>();
        extra.put("%victim%", victim.getName());
        extra.put("%killer%", killer != null ? killer.getName() : "???");

        // 1 tick después para que las estadísticas ya estén actualizadas
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (victim.isOnline()) {
                plugin.getTitleManager().send(victim, "death", extra);
            }
            if (killer != null && killer.isOnline()) {
                plugin.getTitleManager().send(killer, "kill", extra);
            }
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getTitleManager().send(player, "respawn", null);
            }
        }, 5L);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Map<String, String> extra = new HashMap<>();
        extra.put("%from%", event.getFrom().getName());
        plugin.getTitleManager().send(event.getPlayer(), "world-change", extra);
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        if (event.getNewLevel() <= event.getOldLevel()) {
            return;
        }
        Map<String, String> extra = new HashMap<>();
        extra.put("%level%", String.valueOf(event.getNewLevel()));
        plugin.getTitleManager().send(event.getPlayer(), "level-up", extra);
    }
        }
