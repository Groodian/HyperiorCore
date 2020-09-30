package de.groodian.hyperiorcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpawnAble {

    public static List<SpawnAble> spawnAbles = new ArrayList<>();
    private static int MAX_DISTANCE = 50;
    private static int RESPAWN_TIME = 120000;

    private List<Player> showFor;
    private Map<Player, Long> isSetFor;

    protected Location location;
    protected boolean showAll;

    public SpawnAble(Location location, boolean showAll) {
        this.location = location;
        this.showAll = showAll;

        showFor = new ArrayList<>();
        isSetFor = new HashMap<>();

        spawnAbles.add(this);

        if (showAll) {
            showAll();
        }
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
        if (showFor.contains(player)) {
            showFor.remove(player);
        }
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

    private void showAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }
    }

    private void hideAll() {
        for (Player player : showFor) {
            hide(player);
        }
    }

    public void destroy() {
        hideAll();
        spawnAbles.remove(this);
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
