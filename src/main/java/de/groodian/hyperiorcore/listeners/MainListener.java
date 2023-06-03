package de.groodian.hyperiorcore.listeners;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.spawnable.NPCPacketReader;
import de.groodian.hyperiorcore.spawnable.SpawnAble;
import de.groodian.hyperiorcore.user.Rank;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class MainListener implements Listener {

    private static final List<String> COMMANDS_TO_BLOCK = Arrays.asList("/pl", "/plugins", "/bukkit:plugins", "/bukkit:pl", "/bukkit:?",
            "/?", "/icanhasbukkit", "/version", "/ver", "/about", "/bukkit:ver", "/bukkit:version", "/bukkit:about", "/bukkit:help", "/me",
            "/tell", "/minecraft:me", "/minecraft:tell");

    private final Main plugin;

    public MainListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        TextComponent.Builder msg = Component.text();

        if (plugin.getPrefix().isSpectator(player)) {
            msg.append(Component.text("[Spectator] ", NamedTextColor.GRAY));
        }

        Rank rank = plugin.getUserManager().get(player.getUniqueId()).getRank();

        msg.append(rank.longPrefix()
                .append(Component.text(player.getName()))
                .append(Component.text(" » ", NamedTextColor.GRAY))
                .append(e.message().color(NamedTextColor.WHITE)));

        e.renderer((source, sourceDisplayName, message, viewer) -> msg.build());
    }

    @EventHandler
    public void handleAsyncPlayerJoin(AsyncPlayerPreLoginEvent e) {
        plugin.getUserManager().login(e.getUniqueId(), e.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Muss sein da das Scoreboard aus irgendeinem Grund manchmal gespeichert wird
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (SpawnAble spawnAble : plugin.getSpawnAbleManager().getSpawnAbleList()) {
            if (spawnAble.isShowAll()) {
                spawnAble.show(player);
            }
        }

        NPCPacketReader npcPacketReader = new NPCPacketReader(plugin, player);
        npcPacketReader.inject();

        plugin.getPrefix().setPrefix(player);
        plugin.getPrefix().setListName(player);

        plugin.getLevel().updateLevel(player);
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        plugin.getScoreboard().removeFromCache(player);
        for (SpawnAble spawnAble : plugin.getSpawnAbleManager().getSpawnAbleList()) {
            spawnAble.hide(player);
        }

        plugin.getUserManager().logout(player.getUniqueId());
    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        for (SpawnAble spawnAble : plugin.getSpawnAbleManager().getSpawnAbleList()) {
            spawnAble.updateFor(player, e.getTo());
        }
    }

    @EventHandler
    public void handleCommandBlock(PlayerCommandPreprocessEvent e) {
        if (!(plugin.getUserManager().get(e.getPlayer().getUniqueId()).has("commandsblock.bypass"))) {
            String str0 = e.getMessage().split(" ")[0];
            for (String str1 : COMMANDS_TO_BLOCK) {
                if (str0.equalsIgnoreCase(str1)) {
                    e.setCancelled(true);
                    e.getPlayer()
                            .sendMessage(Component.text(
                                    "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.",
                                    NamedTextColor.RED));
                }
            }
        }
    }
}
