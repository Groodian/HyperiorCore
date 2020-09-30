package de.groodian.hyperiorcore.listeners;

import de.groodian.hyperiorcore.util.SpawnAble;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpawnAbleListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            if (spawnAble.isShowAll()) {
                spawnAble.show(player);
            }
        }
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            spawnAble.hide(player);
        }
    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            spawnAble.updateFor(player, e.getTo());
        }
    }

}
