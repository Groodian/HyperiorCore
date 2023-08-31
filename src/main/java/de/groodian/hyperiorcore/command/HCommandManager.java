package de.groodian.hyperiorcore.command;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class HCommandManager<M, C extends HCommand<?, M>> {


    protected final M plugin;
    protected final List<C> hCommands;
    private final Map<UUID, Instant> cooldown;

    public HCommandManager(M plugin) {
        this.plugin = plugin;
        this.hCommands = new ArrayList<>();
        this.cooldown = Collections.synchronizedMap(new HashMap<>());
    }

    protected void registerCommand(C hCommand) {
        hCommand.setHCommandManager(this);
        hCommands.add(hCommand);
    }

    protected boolean checkCall(UUID uuid, long cooldownSeconds) {
        if (uuid == null) {
            return true;
        }

        if (cooldown.containsKey(uuid)) {
            if (Duration.between(cooldown.get(uuid), Instant.now()).getSeconds() < cooldownSeconds) {
                return false;
            } else {
                cooldown.put(uuid, Instant.now());
                return true;
            }
        } else {
            cooldown.put(uuid, Instant.now());
            return true;
        }
    }

    public void removePlayer(UUID uuid) {
        cooldown.remove(uuid);
    }

    public M getPlugin() {
        return plugin;
    }

    public List<C> getHCommands() {
        return hCommands;
    }

}
