package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.HyperiorCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RespawnPlayer {

    public static void respawn(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HyperiorCore.getPaper(), () -> {
            player.spigot().respawn();
        }, 2);
    }

}
