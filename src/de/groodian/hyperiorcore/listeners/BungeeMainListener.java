package de.groodian.hyperiorcore.listeners;

import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeMainListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handleTabComplete(TabCompleteEvent e) {
		if (e.getCursor().startsWith("/")) {
			e.setCancelled(true);
		}
	}
}
