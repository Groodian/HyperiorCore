package de.groodian.hyperiorcore.command;

import de.groodian.hyperiorcore.main.Main;
import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class HCommandManagerPaper extends HCommandManager<Main, HCommandPaper<?>> {

    public HCommandManagerPaper(Main plugin) {
        super(plugin);
    }

    public void registerCommand(JavaPlugin plugin, HCommandPaper hCommand) {
        PluginCommand pluginCommand = Objects.requireNonNull(plugin.getCommand(hCommand.getName()));
        pluginCommand.setExecutor(hCommand);
        pluginCommand.setTabCompleter(hCommand);
        super.registerCommand(hCommand);
    }

}
