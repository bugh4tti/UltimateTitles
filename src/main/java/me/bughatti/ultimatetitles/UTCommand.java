package me.bughatti.ultimatetitles;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UTCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "ultimatetitles.admin";

    private final UltimateTitles plugin;

    public UTCommand(UltimateTitles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        TitleManager manager = plugin.getTitleManager();

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelp(sender);
                return true;

            case "reload":
                plugin.reloadAll();
                sender.sendMessage(plugin.msg("reloaded"));
                return true;

            case "list":
                sender.sendMessage(plugin.raw("list-header"));
                for (String id : manager.getEvents()) {
                    String status = plugin.raw(manager.isEnabled(id) ? "status-on" : "status-off");
                    sender.sendMessage(plugin.raw("list-line", "%event%", id, "%status%", status));
                }
                return true;

            case "on":
            case "off": {
                boolean state = args[0].equalsIgnoreCase("on");
                if (args.length < 2) {
                    sender.sendMessage(plugin.msg("usage-toggle", "%state%", args[0].toLowerCase()));
                    return true;
                }
                String id = args[1].toLowerCase();
                if (!manager.exists(id)) {
                    sender.sendMessage(plugin.msg("unknown-event", "%event%", id));
                    return true;
                }
                manager.setEnabled(id, state);
                sender.sendMessage(plugin.msg(state ? "toggled-on" : "toggled-off", "%event%", id));
                return true;
            }

            case "preview": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.msg("only-player"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.msg("usage-preview"));
                    return true;
                }
                Player player = (Player) sender;
                String id = args[1].toLowerCase();
                if (!manager.exists(id)) {
                    sender.sendMessage(plugin.msg("unknown-event", "%event%", id));
                    return true;
                }
                Map<String, String> extra = new HashMap<>();
                extra.put("%killer%", player.getName());
                extra.put("%victim%", "Jugador");
                extra.put("%from%", "world_nether");
                if (!manager.send(player, id, extra)) {
                    sender.sendMessage(plugin.msg("event-disabled"));
                }
                return true;
            }

            case "send": {
                if (args.length < 3) {
                    sender.sendMessage(plugin.msg("usage-send"));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage(plugin.msg("player-offline"));
                    return true;
                }
                String id = args[2].toLowerCase();
                if (!manager.exists(id)) {
                    sender.sendMessage(plugin.msg("unknown-event", "%event%", id));
                    return true;
                }
                if (!manager.send(target, id, null)) {
                    sender.sendMessage(plugin.msg("event-disabled"));
                    return true;
                }
                sender.sendMessage(plugin.msg("sent", "%event%", id, "%player%", target.getName()));
                return true;
            }

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        for (String line : plugin.rawList("help")) {
            sender.sendMessage(line);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return new ArrayList<>();
        }

        TitleManager manager = plugin.getTitleManager();

        if (args.length == 1) {
            return filter(List.of("help", "reload", "list", "on", "off", "preview", "send"), args[0]);
        }

        String sub = args[0].toLowerCase();

        if (args.length == 2) {
            if (sub.equals("on") || sub.equals("off") || sub.equals("preview")) {
                return filter(manager.getEvents(), args[1]);
            }
            if (sub.equals("send")) {
                List<String> names = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    names.add(p.getName());
                }
                return filter(names, args[1]);
            }
        }

        if (args.length == 3 && sub.equals("send")) {
            return filter(manager.getEvents(), args[2]);
        }

        return new ArrayList<>();
    }

    private List<String> filter(Collection<String> options, String typed) {
        String lower = typed.toLowerCase();
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(lower))
                .sorted()
                .collect(Collectors.toList());
    }
            }
