package de.groodian.hyperiorcore.listeners;

import de.groodian.hyperiorcore.main.BungeeMain;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

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

}
