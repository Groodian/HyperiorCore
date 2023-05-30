package de.groodian.hyperiorcore.command;

import de.groodian.hyperiorcore.main.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class HCommandManagerPaper {

    private final Main plugin;
    private final List<HCommandPaper<? extends CommandSender>> hCommandPapers;

    public HCommandManagerPaper(Main plugin) {
        this.plugin = plugin;
        this.hCommandPapers = new ArrayList<>();
    }

    public void registerCommand(JavaPlugin plugin, HCommandPaper<? extends CommandSender> hCommandPaper) {
        PluginCommand pluginCommand = Objects.requireNonNull(plugin.getCommand(hCommandPaper.getName()));
        pluginCommand.setExecutor(hCommandPaper);
        pluginCommand.setTabCompleter(hCommandPaper);
        hCommandPaper.setCommandManager(this);
        hCommandPapers.add(hCommandPaper);
    }

    public Main getPlugin() {
        return plugin;
    }

    public List<HCommandPaper<? extends CommandSender>> getHCommands() {
        return hCommandPapers;
    }

}
