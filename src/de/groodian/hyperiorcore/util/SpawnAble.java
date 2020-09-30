package de.groodian.hyperiorcore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpawnAble {

    public static List<SpawnAble> spawnAbles = new ArrayList<>();;

    private List<Player> showFor;
    private Map<Player, Long> isSetFor;

    protected Location location;
    protected boolean showAll;

    public SpawnAble(Location location, boolean showAll) {
        this.location = location;
        this.showAll = showAll;

        showFor = new ArrayList<>();
        isSetFor = new HashMap<>();

        if(showAll) {
            showAll();
        }
    }

    public void show(Player player) {
        if(!showFor.contains(player)) {
            showFor.add(player);
        }
        spawnFor(player);
    }

    public void hide(Player player) {
        if(showFor.contains(player)) {
            showFor.remove(player);
        }
        destroyFor(player);
    }

    private void spawnFor(Player player) {
        if(!isSetFor.containsKey(player)) {
            isSetFor.put(player, System.currentTimeMillis());
            System.out.println("spawned for: " + player.getName());
            handleSpawnFor(player);
        }
    }

    protected abstract void handleSpawnFor(Player player);

    private void destroyFor(Player player) {
        if(isSetFor.containsKey(player)) {
            isSetFor.remove(player);
            System.out.println("destroyed for: " + player.getName());
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
        for(Player player: showFor) {
            if(player.getLocation().distance(location) < 70) {
                if(isSetFor.containsKey(player)) {
                    if((System.currentTimeMillis()- isSetFor.get(player)) > 120000) {
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
    }

    public boolean isShowAll() {
        return showAll;
    }

}
