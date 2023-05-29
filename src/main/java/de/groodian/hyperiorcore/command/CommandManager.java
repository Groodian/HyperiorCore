package de.groodian.hyperiorcore.command;

import de.groodian.hyperiorcore.main.Main;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager {

    private final Main plugin;
    private final List<HCommand<? extends CommandSender>> hCommands;

    public CommandManager(Main plugin) {
        this.plugin = plugin;
        this.hCommands = new ArrayList<>();
    }

    public void registerCommand(HCommand<? extends CommandSender> hCommand) {
        Objects.requireNonNull(plugin.getCommand(hCommand.getName())).setExecutor(hCommand);
        hCommand.setCommandManager(this);
        hCommands.add(hCommand);
    }

    public Main getPlugin() {
        return plugin;
    }

    public List<HCommand<? extends CommandSender>> getHCommands() {
        return hCommands;
    }

}
