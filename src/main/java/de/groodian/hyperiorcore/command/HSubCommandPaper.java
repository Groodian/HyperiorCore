package de.groodian.hyperiorcore.command;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public abstract class HSubCommandPaper<C extends HCommandPaper<? extends CommandSender>, T extends CommandSender> {

    private final Class<C> hCommandClazz;
    private final Class<T> commandSenderClazz;
    private final String name;
    private final String permission;
    private final List<String> args;

    protected C hCommand;

    public HSubCommandPaper(Class<C> hCommandClazz, Class<T> commandSenderClazz, String name, String permission, List<String> args) {
        this.hCommandClazz = hCommandClazz;
        this.commandSenderClazz = commandSenderClazz;
        this.name = name;
        this.permission = permission;
        this.args = args;
    }

    public HSubCommandPaper(Class<C> hCommandClazz, Class<T> commandSenderClazz, String name, List<String> args) {
        this(hCommandClazz, commandSenderClazz, name, null, args);
    }

    public void call(CommandSender sender, String[] args) {
        if (commandSenderClazz.isInstance(sender)) {
            onCall(commandSenderClazz.cast(sender), args);
        } else {
            sendMsg(sender, "This command has to be executed by: " + commandSenderClazz.getSimpleName(), NamedTextColor.RED);
        }
    }

    public abstract void onCall(T sender, String[] args);

    protected void sendMsg(CommandSender sender, Component msg) {
        hCommand.sendMsg(sender, msg);
    }

    protected void sendMsg(CommandSender sender, String msg, TextColor color) {
        hCommand.sendMsg(sender, msg, color);
    }

    public void setHCommand(HCommandPaper<? extends CommandSender> hCommandPaper) {
        this.hCommand = hCommandClazz.cast(hCommandPaper);
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getArgs() {
        return args;
    }

}
