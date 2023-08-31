package de.groodian.hyperiorcore.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import de.groodian.hyperiorcore.main.VelocityMain;

public class HCommandManagerVelocity extends HCommandManager<VelocityMain, HCommandVelocity<?>> {

    public HCommandManagerVelocity(VelocityMain plugin) {
        super(plugin);
    }

    public void registerCommand(HCommandVelocity<?> hCommand) {
        CommandManager commandManager = plugin.getServer().getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder(hCommand.getName()).plugin(plugin).build();
        commandManager.register(commandMeta, hCommand);
        super.registerCommand(hCommand);
    }

}
