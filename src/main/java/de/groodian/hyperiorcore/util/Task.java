package de.groodian.hyperiorcore.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Task {

    private final Plugin plugin;
    protected List<Object> cache;

    public Task(Plugin plugin) {
        this.plugin = plugin;
        cache = new ArrayList<>();

        start();
    }

    private void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeAsync();
                finish();
            }
        }.runTaskAsynchronously(plugin);
    }

    public abstract void executeAsync();

    private void finish() {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeSyncOnFinish();
            }
        }.runTask(plugin);
    }

    public abstract void executeSyncOnFinish();

}
