package de.groodian.hyperiorcore.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import de.groodian.hyperiorcore.main.VelocityMain;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;

public abstract class HCommandVelocity<T extends CommandSource> extends HCommand<T, VelocityMain> implements SimpleCommand {

    public HCommandVelocity(Class<T> clazz, String name, String description, Component prefix, String permission, long cooldownSeconds,
                            List<HCommand<T, VelocityMain>> hSubCommands, List<HArgument> hArguments) {
        super(clazz, name, description, prefix, permission, cooldownSeconds, hSubCommands, hArguments);
    }

    public HCommandVelocity(Class<T> clazz, String name, String description, Component prefix, String permission,
                            List<HCommand<T, VelocityMain>> hSubCommands, List<HArgument> hArguments) {
        super(clazz, name, description, prefix, permission, HCommand.COOLDOWN_SECONDS, hSubCommands, hArguments);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = combineArgs(invocation.arguments());

        call((T) source, args);
    }

    @Override
    protected UUID getUUID(T sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }
        return null;
    }

    @Override
    protected boolean onPermissionCheck(CommandSource sender, String permission) {
        if (sender instanceof Player player) {
            return hCommandManager.getPlugin().getUserManager().get(player.getUniqueId()).has(permission);
        } else if (sender instanceof ConsoleCommandSource) {
            return true;
        }

        return false;
    }

    @Override
    protected void onMessageSend(CommandSource sender, Component msg) {
        sender.sendMessage(msg);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }

}
