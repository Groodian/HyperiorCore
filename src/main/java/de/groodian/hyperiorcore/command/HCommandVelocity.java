package de.groodian.hyperiorcore.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;


public abstract class HCommandVelocity<T extends CommandSource> implements SimpleCommand {

    private final Class<T> clazz;
    private final String name;
    private final String description;
    private final Component prefix;
    private final String permission;
    private final List<HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource>> hSubCommandsVelocity;
    private final List<HArgument> hArguments;

    private HCommandManagerVelocity hCommandManagerVelocity;

    public HCommandVelocity(Class<T> clazz, String name, String description, Component prefix, String permission,
                            List<HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource>> hSubCommandsVelocity,
                            List<HArgument> hArguments) {
        this.clazz = clazz;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.permission = permission;
        this.hSubCommandsVelocity = hSubCommandsVelocity;
        this.hArguments = hArguments;

        for (HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource> hSubCommandVelocity : hSubCommandsVelocity) {
            hSubCommandVelocity.setHCommand(this);
        }
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = combineArgs(invocation.arguments());

        if (!checkPermission(source, permission)) {
            return;
        }

        if (hSubCommandsVelocity.isEmpty()) {
            if (clazz.isInstance(source)) {
                if (args.length == hArguments.size()) {
                    onCall(clazz.cast(source), args);
                } else {
                    sendGlobalUsage(source);
                }
            } else {
                sendMsg(source, "This command has to be executed by: " + clazz.getSimpleName(), NamedTextColor.RED);
            }

            return;
        }

        if (args.length >= 1) {
            boolean found = false;
            for (HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource> hSubCommandPaper : hSubCommandsVelocity) {
                if (hSubCommandPaper.getName().equalsIgnoreCase(args[0])) {
                    if (!checkPermission(source, hSubCommandPaper.getPermission())) {
                        return;
                    }

                    if (args.length == 1 + hSubCommandPaper.getArgs().size()) {
                        hSubCommandPaper.call(source, Arrays.copyOfRange(args, 1, args.length));
                    } else {
                        sendSubCommandUsage(source, hSubCommandPaper);
                    }

                    found = true;
                    break;
                }
            }

            if (!found) {
                sendGlobalUsage(source);
            }
        } else {
            sendGlobalUsage(source);
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    protected abstract void onCall(T sender, String[] args);

    private String[] combineArgs(String[] args) {
        List<String> newArgs = new ArrayList<>();

        StringBuilder currentCombineArg = new StringBuilder();
        boolean inMultiWord = false;
        for (String arg : args) {
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                String trimArg = arg.substring(1, arg.length() - 1);
                if (inMultiWord) {
                    currentCombineArg.append(" ").append(trimArg);
                } else {
                    newArgs.add(trimArg);
                }
            } else if (arg.startsWith("\"") && !inMultiWord) {
                inMultiWord = true;
                currentCombineArg.append(arg, 1, arg.length());
            } else if (arg.endsWith("\"") && inMultiWord) {
                inMultiWord = false;
                currentCombineArg.append(" ").append(arg, 0, arg.length() - 1);
                newArgs.add(currentCombineArg.toString());
                currentCombineArg = new StringBuilder();
            } else {
                if (inMultiWord) {
                    currentCombineArg.append(" ").append(arg);
                } else {
                    newArgs.add(arg);
                }
            }
        }

        return newArgs.toArray(new String[0]);
    }

    private void sendGlobalUsage(CommandSource source) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!hSubCommandsVelocity.isEmpty()) {
            stringBuilder.append(" <");
            for (HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource> hSubCommandVelocity : hSubCommandsVelocity) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append("/");
                }
                stringBuilder.append(hSubCommandVelocity.getName());
            }
            stringBuilder.append(">");
        } else {
            for (HArgument hArgument : hArguments) {
                stringBuilder.append(" ");
                if (hArgument.isMultipleWords())
                    stringBuilder.append("\"");
                stringBuilder.append("<").append(hArgument.getName()).append(">");
                if (hArgument.isMultipleWords())
                    stringBuilder.append("\"");
            }
        }

        sendMsg(source, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + stringBuilder, NamedTextColor.GOLD)));
    }

    private void sendSubCommandUsage(CommandSource source,
                                     HSubCommandVelocity<? extends HCommandVelocity<? extends CommandSource>, ? extends CommandSource> hSubCommandVelocity) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String args : hSubCommandVelocity.getArgs()) {
            stringBuilder.append(" <").append(args).append(">");
        }

        sendMsg(source, Component.text("Usage: ", NamedTextColor.RED)
                .append(Component.text("/" + name + " " + hSubCommandVelocity.getName() + stringBuilder, NamedTextColor.GOLD)));
    }

    private boolean checkPermission(CommandSource source, String permission) {
        if (source instanceof Player player) {
            if (hCommandManagerVelocity.getPlugin().getUserManager().get(player.getUniqueId()).has(permission)) {
                return true;
            }
        } else if (source instanceof ConsoleCommandSource) {
            return true;
        }

        sendMsg(source, "You don't have permission to do that.", NamedTextColor.RED);

        return false;
    }

    protected void sendMsg(CommandSource source, Component msg) {
        source.sendMessage(prefix.append(msg));
    }

    protected void sendMsg(CommandSource source, String msg, TextColor color) {
        source.sendMessage(prefix.append(Component.text(msg, color)));
    }

    public void setCommandManager(HCommandManagerVelocity hCommandManagerVelocity) {
        this.hCommandManagerVelocity = hCommandManagerVelocity;
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

    @Override
    public List<String> suggest(Invocation invocation) {
        return SimpleCommand.super.suggest(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }

}
