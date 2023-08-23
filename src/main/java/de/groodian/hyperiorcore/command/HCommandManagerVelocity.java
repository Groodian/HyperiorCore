package de.groodian.hyperiorcore.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import de.groodian.hyperiorcore.main.VelocityMain;
import java.util.ArrayList;
import java.util.List;

public class HCommandManagerVelocity {

    private final VelocityMain plugin;
    private final List<HCommandVelocity<? extends CommandSource>> hCommandsVelocity;


    public HCommandManagerVelocity(VelocityMain plugin) {
        this.plugin = plugin;
        this.hCommandsVelocity = new ArrayList<>();
    }

    public void registerCommand(HCommandVelocity<? extends CommandSource> hCommandVelocity) {
        CommandManager commandManager = plugin.getServer().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(hCommandVelocity.getName()).plugin(plugin).build();
        commandManager.register(commandMeta, hCommandVelocity);
        hCommandVelocity.setCommandManager(this);
        hCommandsVelocity.add(hCommandVelocity);
    }

    public VelocityMain getPlugin() {
        return plugin;
    }

    public List<HCommandVelocity<? extends CommandSource>> getHCommands() {
        return hCommandsVelocity;
    }

}
