package com.example.hapisplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HapisPlugin extends JavaPlugin {
    private final Map<UUID, Integer> jailTimes = new HashMap<>();
    private final Map<UUID, BukkitTask> jailTasks = new HashMap<>();

    private final Location jailLocation = new Location(Bukkit.getWorlds().get(0), -32, 248, -45);
    private final Location releaseLocation = new Location(Bukkit.getWorlds().get(0), -21, 246, -45);

    @Override
    public void onEnable() {
        getCommand("hapis").setExecutor(new HapisCommand(this));
    }

    public void jailPlayer(Player player, int seconds) {
        UUID uuid = player.getUniqueId();
        boolean alreadyJailed = jailTimes.containsKey(uuid);

        jailTimes.put(uuid, jailTimes.getOrDefault(uuid, 0) + seconds);
        player.teleport(jailLocation);

        if (!alreadyJailed) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
                int timeLeft = jailTimes.getOrDefault(uuid, 0);
                if (timeLeft <= 0) {
                    releasePlayer(player);
                } else {
                    jailTimes.put(uuid, timeLeft - 1);
                    player.sendTitle("§cHapis Süresi", "§e" + jailTimes.get(uuid) + " saniye kaldı", 0, 20, 0);
                }
            }, 0L, 20L);
            jailTasks.put(uuid, task);
        }
    }

    public void reduceJailTime(Player player, int seconds) {
        UUID uuid = player.getUniqueId();
        if (!jailTimes.containsKey(uuid)) return;

        int current = jailTimes.get(uuid);
        int newTime = Math.max(0, current - seconds);
        jailTimes.put(uuid, newTime);
        player.sendTitle("§cHapis Süresi", "§e" + newTime + " saniye kaldı", 0, 20, 0);

        if (newTime <= 0) {
            releasePlayer(player);
        }
    }

    public void releasePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        jailTimes.remove(uuid);
        BukkitTask task = jailTasks.remove(uuid);
        if (task != null) task.cancel();
        player.teleport(releaseLocation);
        player.sendTitle("§aSerbest!", "§7Artık özgürsün!", 10, 40, 10);
    }

    public boolean isJailed(Player player) {
        return jailTimes.containsKey(player.getUniqueId());
    }
}