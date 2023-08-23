package de.groodian.hyperiorcore.command;

import com.velocitypowered.api.command.CommandSource;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public abstract class HSubCommandVelocity<C extends HCommandVelocity<? extends CommandSource>, T extends CommandSource> {

    private final Class<C> hCommandClazz;
    private final Class<T> commandSenderClazz;
    private final String name;
    private final String permission;
    private final List<String> args;

    protected C hCommand;

    public HSubCommandVelocity(Class<C> hCommandClazz, Class<T> commandSenderClazz, String name, String permission, List<String> args) {
        this.hCommandClazz = hCommandClazz;
        this.commandSenderClazz = commandSenderClazz;
        this.name = name;
        this.permission = permission;
        this.args = args;
    }

    public HSubCommandVelocity(Class<C> hCommandClazz, Class<T> commandSenderClazz, String name, List<String> args) {
        this(hCommandClazz, commandSenderClazz, name, null, args);
    }

    public void call(CommandSource source, String[] args) {
        if (commandSenderClazz.isInstance(source)) {
            onCall(commandSenderClazz.cast(source), args);
        } else {
            sendMsg(source, "This command has to be executed by: " + commandSenderClazz.getSimpleName(), NamedTextColor.RED);
        }
    }

    public abstract void onCall(T sender, String[] args);

    protected void sendMsg(CommandSource source, Component msg) {
        hCommand.sendMsg(source, msg);
    }

    protected void sendMsg(CommandSource source, String msg, TextColor color) {
        hCommand.sendMsg(source, msg, color);
    }

    public void setHCommand(HCommandVelocity<? extends CommandSource> hCommandPaper) {
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
