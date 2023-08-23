package de.groodian.hyperiorcore.main;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.groodian.hyperiorcore.command.HCommandManagerVelocity;
import de.groodian.hyperiorcore.listeners.MainListenerVelocity;
import de.groodian.hyperiorcore.user.UserManager;
import de.groodian.hyperiorcore.util.DatabaseManager;
import org.slf4j.Logger;

@Plugin(
        id = "hyperiorcore",
        name = "HyperiorCore",
        version = "5.0.0-SNAPSHOT",
        description = "Core functions for Hyperior",
        authors = {"Groodian"}
)
public class VelocityMain {

    private static VelocityMain instance;

    private final ProxyServer server;
    private final Logger logger;
    private HCommandManagerVelocity hCommandManagerVelocity;
    private DatabaseManager databaseManager;
    private UserManager userManager;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        instance = this;

        Mode.setModeType(ModeType.VELOCITY);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Output.send(Main.PREFIX + "§aDas Plugin wird geladen...");

        hCommandManagerVelocity = new HCommandManagerVelocity(this);
        databaseManager = new DatabaseManager(HyperiorCore.DB_ADDRESS, HyperiorCore.DB_PORT, HyperiorCore.DB_DATABASE, HyperiorCore.DB_USER,
                HyperiorCore.DB_PASSWORD);
        userManager = new UserManager(databaseManager);

        server.getEventManager().register(this, new MainListenerVelocity(this));

        Output.send(Main.PREFIX + "§aGeladen!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        Output.send(Main.PREFIX + "§cDas Plugin wird gestoppt...");

        databaseManager.disconnect();

        Output.send(Main.PREFIX + "§cGestoppt!");
    }

    public static VelocityMain getInstance() {
        return instance;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public HCommandManagerVelocity getHCommandManagerVelocity() {
        return hCommandManagerVelocity;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
