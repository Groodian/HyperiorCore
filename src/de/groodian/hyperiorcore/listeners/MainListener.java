package de.groodian.hyperiorcore.listeners;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.ranks.Rank;
import de.groodian.hyperiorcore.util.SpawnAble;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;
import java.util.List;

public class MainListener implements Listener {

    private static final List<String> COMMANDS_TO_BLOCK = Arrays.asList("/pl", "/plugins", "/bukkit:plugins", "/bukkit:pl", "/bukkit:?", "/?", "/icanhasbukkit", "/version", "/ver", "/about", "/bukkit:ver", "/bukkit:version", "/bukkit:about", "/bukkit:help", "/me", "/tell", "/minecraft:me", "/minecraft:tell", "/help");

    private Main plugin;

    public MainListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String spec = "";
        if (plugin.getPrefix().getSpectators().contains(player)) {
            spec = "�7[Spectator] ";
        }
        Rank rank = plugin.getRanks().get(player.getUniqueId());
        e.setFormat(spec + rank.getLongPrefix() + player.getName() + " �7� �r" + e.getMessage());
    }

    @EventHandler
    public void handleAsyncPlayerJoin(AsyncPlayerPreLoginEvent e) {
        plugin.getRanks().login(e.getUniqueId());
        plugin.getCoinSystem().login(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Muss sein da das Scoreboard aus irgendeinem Grund manchmal gespeichert wird
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            if (spawnAble.isShowAll()) {
                spawnAble.show(player);
            }
        }

        plugin.getPrefix().setPrefix(player);
        plugin.getPrefix().setListName(player);

        plugin.getLevel().updateLevel(player);

    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        plugin.getScoreboard().removeFromCache(player);
        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            spawnAble.hide(player);
        }

        plugin.getRanks().logout(player.getUniqueId());
        plugin.getCoinSystem().logout(player.getUniqueId());

    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
            spawnAble.updateFor(player, e.getTo());
        }
    }

    @EventHandler
    public void handleCommandBlock(PlayerCommandPreprocessEvent e) {
        if (!(plugin.getRanks().has(e.getPlayer().getUniqueId(), "commandsblock.bypass"))) {
            String str0 = e.getMessage().split(" ")[0];
            for (String str1 : COMMANDS_TO_BLOCK) {
                if (str0.equalsIgnoreCase(str1)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("�cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
                }
            }
        }
    }
}
