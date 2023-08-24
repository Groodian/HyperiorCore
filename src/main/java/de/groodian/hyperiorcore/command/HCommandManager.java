package de.groodian.hyperiorcore.command;

import java.util.ArrayList;
import java.util.List;

public abstract class HCommandManager<M, C extends HCommand<?, M>> {

    protected final M plugin;
    protected final List<C> hCommands;

    public HCommandManager(M plugin) {
        this.plugin = plugin;
        this.hCommands = new ArrayList<>();
    }

    protected void registerCommand(C hCommand) {
        hCommand.setHCommandManager(this);
        hCommands.add(hCommand);
    }

    public M getPlugin() {
        return plugin;
    }

    public List<C> getHCommands() {
        return hCommands;
    }

}
