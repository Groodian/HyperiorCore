package de.groodian.hyperiorcore.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public abstract class HCommand<T, M> {

    protected static final long COOLDOWN_SECONDS = 2;

    protected final Class<T> clazz;
    protected final String name;
    protected final String description;
    protected final Component prefix;
    protected final String permission;
    protected final long cooldownSeconds;
    protected final List<HCommand<T, M>> hSubCommands;
    protected final List<HArgument> hArguments;

    protected HCommandManager<M, ?> hCommandManager;
    protected HCommand<T, M> hTopCommand;

    public HCommand(Class<T> clazz, String name, String description, Component prefix, String permission, long cooldownSeconds,
                    List<HCommand<T, M>> hSubCommands, List<HArgument> hArguments) {
        this.clazz = clazz;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.permission = permission;
        this.cooldownSeconds = cooldownSeconds;
        this.hSubCommands = hSubCommands;
        this.hArguments = hArguments;

        for (HCommand<T, M> hSubCommand : hSubCommands) {
            hSubCommand.setHTopCommand(this);
        }
    }

    public void call(T sender, String[] args) {
        if (hTopCommand == null) {
            if (!hCommandManager.checkCall(getUUID(sender), cooldownSeconds)) {
                sendMsg(sender, "This command is on cooldown, try again in a few seconds.", NamedTextColor.RED);
                return;
            }
        }

        if (!checkPermission(sender, permission)) {
            return;
        }

        if (hSubCommands.isEmpty()) {
            if (clazz.isInstance(sender)) {
                int hArgumentOptional = 0;
                for (HArgument hArgument : hArguments) {
                    if (hArgument.isOptional()) {
                        hArgumentOptional++;
                    }
                }

                if (args.length <= hArguments.size() && args.length >= hArguments.size() - hArgumentOptional) {
                    onCall(clazz.cast(sender), args);
                } else {
                    sendUsage(sender);
                }
            } else {
                sendMsg(sender, "This command has to be executed by: " + clazz.getSimpleName(), NamedTextColor.RED);
            }

            return;
        }

        if (args.length >= 1) {
            boolean found = false;
            for (HCommand<T, M> hSubCommand : hSubCommands) {
                if (hSubCommand.getName().equalsIgnoreCase(args[0])) {
                    if (!checkPermission(sender, hSubCommand.getPermission())) {
                        return;
                    }

                    hSubCommand.call(sender, Arrays.copyOfRange(args, 1, args.length));

                    found = true;
                    break;
                }
            }

            if (!found) {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    protected abstract UUID getUUID(T sender);

    protected abstract void onCall(T sender, String[] args);

    protected abstract void onMessageSend(T sender, Component msg);

    protected abstract boolean onPermissionCheck(T sender, String permission);

    protected <A extends T> A castSender(Class<A> clazz, T sender) {
        if (clazz.isInstance(sender)) {
            return clazz.cast(sender);
        } else {
            sendMsg(sender, "This command has to be executed by: " + clazz.getSimpleName(), NamedTextColor.RED);
            return null;
        }
    }

    protected void sendMsg(T sender, Component msg) {
        onMessageSend(sender, prefix.append(msg));
    }

    protected void sendMsg(T sender, String msg, TextColor color) {
        onMessageSend(sender, prefix.append(Component.text(msg, color)));
    }

    private boolean checkPermission(T sender, String permission) {
        if (onPermissionCheck(sender, permission)) {
            return true;
        } else {
            sendMsg(sender, "You don't have permission to do that.", NamedTextColor.RED);
            return false;
        }
    }

    private void sendUsage(T sender) {
        TextComponent.Builder usage = Component.text();

        StringBuilder commandStack = new StringBuilder();
        HCommand<T, M> hCommandCurrent = this;
        while (hCommandCurrent != null) {
            if (commandStack.length() > 0) {
                commandStack.insert(0, " ");
            }
            commandStack.insert(0, hCommandCurrent.getName());
            hCommandCurrent = hCommandCurrent.getHTopCommand();
        }

        if (hSubCommands.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("/").append(commandStack);
            for (HArgument hArgument : hArguments) {
                stringBuilder.append(" ");
                if (hArgument.isOptional()) {
                    stringBuilder.append("[");
                }
                if (hArgument.isMultipleWords()) {
                    stringBuilder.append("\"");
                }
                stringBuilder.append("<").append(hArgument.getName()).append(">");
                if (hArgument.isMultipleWords()) {
                    stringBuilder.append("\"");
                }
                if (hArgument.isOptional()) {
                    stringBuilder.append("]");
                }
            }

            usage.append(Component.text(stringBuilder.toString(), NamedTextColor.GOLD));
        } else {
            for (HCommand<T, M> hSubCommand : hSubCommands) {
                usage.appendNewline()
                        .append(Component.text("   »", NamedTextColor.GOLD))
                        .append(Component.text(" /" + commandStack + " " + hSubCommand.getName(), NamedTextColor.AQUA))
                        .append(Component.text(" - " + hSubCommand.getDescription(), NamedTextColor.GRAY));
            }
        }

        sendMsg(sender, Component.text("Usage: ", NamedTextColor.RED).append(usage.build()));
    }

    protected String[] combineArgs(String[] args) {
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

    public List<HCommand<T, M>> getHSubCommands() {
        return hSubCommands;
    }

    public List<HArgument> getHArguments() {
        return hArguments;
    }

    public HCommandManager<M, ?> getHCommandManager() {
        return hCommandManager;
    }

    public void setHCommandManager(HCommandManager<M, ?> hCommandManager) {
        this.hCommandManager = hCommandManager;
        for (HCommand<T, M> hSubCommand : hSubCommands) {
            hSubCommand.setHCommandManager(hCommandManager);
        }
    }

    public HCommand<T, M> getHTopCommand() {
        return hTopCommand;
    }

    public void setHTopCommand(HCommand<T, M> hTopCommand) {
        this.hTopCommand = hTopCommand;
    }

}
