package de.groodian.hyperiorcore.command;

import de.groodian.hyperiorcore.main.Output;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HCommandPaper<T extends CommandSender> implements CommandExecutor, TabCompleter {

    private final Class<T> clazz;
    private final String name;
    private final String description;
    private final Component prefix;
    private final String permission;
    private final List<HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender>> hSubCommandPapers;

    private HCommandManagerPaper HCommandManagerPaper;

    public HCommandPaper(Class<T> clazz, String name, String description, Component prefix, String permission,
                         List<HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender>> hSubCommandPapers) {
        this.clazz = clazz;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.permission = permission;
        this.hSubCommandPapers = hSubCommandPapers;

        for (HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender> hSubCommandPaper : hSubCommandPapers) {
            hSubCommandPaper.setHCommand(this);
        }
    }

    public HCommandPaper(Class<T> clazz, String name, String description, Component prefix,
                         List<HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender>> hSubCommandPapers) {
        this(clazz, name, description, prefix, null, hSubCommandPapers);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender || sender instanceof Player) {
            if (!checkPermission(sender, permission)) {
                return false;
            }

            if (hSubCommandPapers.isEmpty()) {
                if (clazz.isInstance(sender)) {
                    onCall(clazz.cast(sender));
                } else {
                    sendMsg(sender, "This command has to be executed by: " + clazz.getSimpleName(), NamedTextColor.RED);
                }

                return false;
            }

            if (args.length >= 1) {
                boolean found = false;
                for (HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender> hSubCommandPaper : hSubCommandPapers) {
                    if (hSubCommandPaper.getName().equalsIgnoreCase(args[0])) {
                        if (!checkPermission(sender, hSubCommandPaper.getPermission())) {
                            return false;
                        }

                        if (args.length == 1 + hSubCommandPaper.getArgs().size()) {
                            hSubCommandPaper.call(sender, Arrays.copyOfRange(args, 1, args.length));
                        } else {
                            sendSubCommandUsage(sender, hSubCommandPaper);
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                                @NotNull String[] args) {
        List<String> tapComplete = new ArrayList<>();

        Output.send("TEST " + args.length);

        if (args.length == 1) {
            for (HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender> hSubCommandPaper : hSubCommandPapers) {
                tapComplete.add(hSubCommandPaper.getName());
            }
        }

        return tapComplete;
    }

    protected abstract void onCall(T sender);

    private void sendGlobalUsage(CommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        for (HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender> hSubCommandPaper : hSubCommandPapers) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("/");
            }
            stringBuilder.append(hSubCommandPaper.getName());
        }

        sendMsg(sender, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + " <" + stringBuilder + ">", NamedTextColor.GOLD)));
    }

    private void sendSubCommandUsage(CommandSender sender,
                                     HSubCommandPaper<? extends HCommandPaper<? extends CommandSender>, ? extends CommandSender> hSubCommandPaper) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String args : hSubCommandPaper.getArgs()) {
            stringBuilder.append(" <").append(args).append(">");
        }

        sendMsg(sender, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + " " + hSubCommandPaper.getName() + stringBuilder, NamedTextColor.GOLD)));
    }

    private boolean checkPermission(CommandSender sender, String permission) {
        if (sender instanceof Player player) {
            if (HCommandManagerPaper.getPlugin().getUserManager().get(player.getUniqueId()).has(permission)) {
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

    public void setCommandManager(HCommandManagerPaper HCommandManagerPaper) {
        this.HCommandManagerPaper = HCommandManagerPaper;
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
