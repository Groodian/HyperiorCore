package de.groodian.hyperiorcore.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class HCommand<T extends CommandSender> implements CommandExecutor {

    private final Class<T> clazz;
    private final String name;
    private final String description;
    private final Component prefix;
    private final String permission;
    private final List<HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender>> hSubCommands;

    private CommandManager commandManager;

    public HCommand(Class<T> clazz, String name, String description, Component prefix, String permission, List<HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender>> hSubCommands) {
        this.clazz = clazz;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.permission = permission;
        this.hSubCommands = hSubCommands;

        for (HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender> hSubCommand : hSubCommands) {
            hSubCommand.setHCommand(this);
        }
    }

    public HCommand(Class<T> clazz, String name, String description, Component prefix, List<HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender>> hSubCommands) {
        this(clazz, name, description, prefix, null, hSubCommands);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender || sender instanceof Player) {
            if (!checkPermission(sender, permission)) {
                return false;
            }

            if (hSubCommands.isEmpty()) {
                if (clazz.isInstance(sender)) {
                    onCall(clazz.cast(sender));
                } else {
                    sendMsg(sender, "This command has to be executed by: " + clazz.getSimpleName(), NamedTextColor.RED);
                }

                return false;
            }

            if (args.length >= 1) {
                boolean found = false;
                for (HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender> hSubCommand : hSubCommands) {
                    if (hSubCommand.getName().equalsIgnoreCase(args[0])) {
                        if (!checkPermission(sender, hSubCommand.getPermission())) {
                            return false;
                        }

                        if (args.length == 1 + hSubCommand.getArgs().size()) {
                            hSubCommand.call(sender, Arrays.copyOfRange(args, 1, args.length));
                        } else {
                            sendSubCommandUsage(sender, hSubCommand);
                        }

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    sendGlobalUsage(sender);
                }
            } else {
                sendGlobalUsage(sender);
            }

        } else {
            sendMsg(sender, "This command has to be executed by a player or the console.", NamedTextColor.RED);
        }

        return false;
    }

    protected abstract void onCall(T sender);

    private void sendGlobalUsage(CommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        for (HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender> hSubCommand : hSubCommands) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("/");
            }
            stringBuilder.append(hSubCommand.getName());
        }

        sendMsg(sender, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + " <" + stringBuilder + ">", NamedTextColor.GOLD)));
    }

    private void sendSubCommandUsage(CommandSender sender, HSubCommand<? extends HCommand<? extends CommandSender>, ? extends CommandSender> hSubCommand) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String args : hSubCommand.getArgs()) {
            stringBuilder.append(" <").append(args).append(">");
        }

        sendMsg(sender, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + " " + hSubCommand.getName() + stringBuilder, NamedTextColor.GOLD)));
    }

    private boolean checkPermission(CommandSender sender, String permission) {
        if (sender instanceof Player player) {
            if (commandManager.getPlugin().getUserManager().get(player.getUniqueId()).has(permission)) {
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        sendMsg(sender, "You don't have permission to do that.", NamedTextColor.RED);

        return false;
    }

    protected void sendMsg(CommandSender sender, Component msg) {
        sender.sendMessage(prefix.append(msg));
    }

    protected void sendMsg(CommandSender sender, String msg, TextColor color) {
        sender.sendMessage(prefix.append(Component.text(msg, color)));
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Component getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }

}
