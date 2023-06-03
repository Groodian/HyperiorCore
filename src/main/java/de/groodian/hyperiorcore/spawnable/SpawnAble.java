package de.groodian.hyperiorcore.spawnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SpawnAble {

    private static final int MAX_DISTANCE = 50;
    private static final int RESPAWN_TIME = 120000;

    protected Location location;

    private boolean showAll;
    private final List<Player> showFor;
    private final Map<Player, Long> isSetFor;

    public SpawnAble(Location location) {
        this.location = location;

        showAll = false;
        showFor = new ArrayList<>();
        isSetFor = new HashMap<>();
    }

    public void showAll() {
        showAll = true;
        showForAll();
    }

    public void show(Player player) {
        if (!showFor.contains(player)) {
            showFor.add(player);
        }
        if (player.getLocation().distance(location) < MAX_DISTANCE) {
            spawnFor(player);
        }
    }

    public void hide(Player player) {
        showFor.remove(player);
        destroyFor(player);
    }

    private void spawnFor(Player player) {
        if (!isSetFor.containsKey(player)) {
            isSetFor.put(player, System.currentTimeMillis());
            //System.out.println("spawned for: " + player.getName());
            handleSpawnFor(player);
        }
    }

    protected abstract void handleSpawnFor(Player player);

    private void destroyFor(Player player) {
        if (isSetFor.containsKey(player)) {
            isSetFor.remove(player);
            //System.out.println("destroyed for: " + player.getName());
            handleDestroyFor(player);
        }
    }

    protected abstract void handleDestroyFor(Player player);

    private void showForAll() {
        List<Player> temp = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (Player player : temp) {
            show(player);
        }
    }

    private void hideForAll() {
        List<Player> temp = new ArrayList<>(showFor);

        for (Player player : temp) {
            hide(player);
        }
    }

    public void update() {
        for (Player player : showFor) {
            updateFor(player);
        }
    }

    public void updateFor(Player player) {
        updateFor(player, player.getLocation());
    }

    public void updateFor(Player player, Location playerLocation) {
        if (playerLocation.distance(location) < MAX_DISTANCE) {
            if (isSetFor.containsKey(player)) {
                if ((System.currentTimeMillis() - isSetFor.get(player)) > RESPAWN_TIME) {
                    destroyFor(player);
                    spawnFor(player);
                }
            } else {
                spawnFor(player);
            }
        } else {
            destroyFor(player);
        }
    }

    public boolean isShowAll() {
        return showAll;
    }

}
