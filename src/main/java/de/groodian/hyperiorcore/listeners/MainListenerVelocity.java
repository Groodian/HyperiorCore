package de.groodian.hyperiorcore.listeners;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import de.groodian.hyperiorcore.main.VelocityMain;

public class MainListenerVelocity {

    private final VelocityMain plugin;

    public MainListenerVelocity(VelocityMain plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent e, Continuation continuation) {
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            Player player = e.getPlayer();
            plugin.getUserManager().login(player.getUniqueId(), player.getUsername());
            continuation.resume();
        }).schedule();
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisconnect(DisconnectEvent e, Continuation continuation) {
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            Player player = e.getPlayer();
            plugin.getUserManager().logout(player.getUniqueId());
            continuation.resume();
        }).schedule();
    }

}
