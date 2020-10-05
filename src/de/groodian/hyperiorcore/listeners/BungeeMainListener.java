package de.groodian.hyperiorcore.listeners;

import de.groodian.hyperiorcore.main.BungeeMain;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class BungeeMainListener implements Listener {

    private BungeeMain plugin;

    public BungeeMainListener(BungeeMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleTabComplete(TabCompleteEvent e) {
        if (e.getCursor().startsWith("/")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handlePlayerJoin(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        e.registerIntent(plugin);
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> plugin.getRanks().login(uuid));
        e.completeIntent(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        e.registerIntent(plugin);
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> plugin.getRanks().logout(uuid));
        e.completeIntent(plugin);
    }

}
