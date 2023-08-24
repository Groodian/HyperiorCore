package de.groodian.hyperiorcore.command;

import de.groodian.hyperiorcore.main.Main;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HCommandPaper<T extends CommandSender> extends HCommand<T, Main> implements CommandExecutor, TabCompleter {

    public HCommandPaper(Class<T> clazz, String name, String description, Component prefix, String permission,
                         List<HCommand<T, Main>> hSubCommands, List<HArgument> hArguments) {
        super(clazz, name, description, prefix, permission, hSubCommands, hArguments);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender || sender instanceof Player) {
            args = combineArgs(args);
            call((T) sender, args);
        } else {
            sendMsg((T) sender, "This command has to be executed by a player or the console.", NamedTextColor.RED);
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                                @NotNull String[] args) {
        List<String> tapComplete = new ArrayList<>();

        if (args.length == 1) {
            for (HCommand<T, Main> hSubCommandPaper : hSubCommands) {
                tapComplete.add(hSubCommandPaper.getName());
            }
        }

        return tapComplete;
    }


    @Override
    protected boolean onPermissionCheck(CommandSender sender, String permission) {
        if (sender instanceof Player player) {
            return hCommandManager.getPlugin().getUserManager().get(player.getUniqueId()).has(permission);
        } else if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        return false;
    }

    @Override
    protected void onMessageSend(CommandSender sender, Component msg) {
        sender.sendMessage(msg);
    }

}
