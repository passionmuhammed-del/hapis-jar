package com.example.hapisplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HapisCommand implements CommandExecutor {
    private final HapisPlugin plugin;

    public HapisCommand(HapisPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            try {
                int seconds = Integer.parseInt(args[0]);
                plugin.jailPlayer(player, seconds);
                player.sendMessage("§c" + seconds + " saniye hapis cezası aldın!");
            } catch (NumberFormatException e) {
                player.sendMessage("§cSüreyi sayı olarak girmelisin.");
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("çıkart")) {
            try {
                int seconds = Integer.parseInt(args[1]);
                if (plugin.isJailed(player)) {
                    plugin.reduceJailTime(player, seconds);
                    player.sendMessage("§aHapis süren " + seconds + " saniye azaldı.");
                } else {
                    player.sendMessage("§cZaten hapiste değilsin!");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cSüreyi sayı olarak girmelisin.");
            }
            return true;
        }

        player.sendMessage("§eKullanım: /hapis [saniye] veya /hapis çıkart [saniye]");
        return true;
    }
}