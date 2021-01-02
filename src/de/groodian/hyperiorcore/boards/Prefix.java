package de.groodian.hyperiorcore.boards;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.ranks.Rank;
import de.groodian.hyperiorcore.util.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Prefix {

    private Main plugin;

    private List<Player> spectators;

    public Prefix(Main plugin) {
        this.plugin = plugin;
        this.spectators = new ArrayList<>();
    }

    public void setPrefix(Player player) {

        // Hohle von jedem Spieler das Scoreboard und adde den neuen Spieler, wenn ein
        // Spieler "neu" ist werden für diesen Spieler alle Spieler die
        // bereits auf dem Server
        // sind zu seinem Scoreboard hinzugefügt
        for (Player all : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreboard = all.getScoreboard();

            if (all == player) {
                for (Player all1 : Bukkit.getOnlinePlayers()) {
                    if (all1 != player) {
                        editScoreboard(scoreboard, all1);
                    }
                }

            }

            editScoreboard(scoreboard, player);

        }

    }

    private void editScoreboard(Scoreboard scoreboard, Player player) {

        Rank rank = plugin.getRanks().get(player.getUniqueId());
        String teamName = rank.getValue() + "-" + rank.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setPrefix(rank.getShortPrefix());
        }

        Team spectator = scoreboard.getTeam("999-Spectator");
        if (spectator == null) {
            spectator = scoreboard.registerNewTeam("999-Spectator");
            spectator.setPrefix("§7§o");
            spectator.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
            spectator.setCanSeeFriendlyInvisibles(true);
        }

        if (!spectators.contains(player)) {
            player.setDisplayName(rank.getColor() + player.getName() + "§r");
            team.addEntry(player.getName());
        } else {
            player.setDisplayName("§7" + player.getName() + "§r");
            spectator.addEntry(player.getName());
        }

    }

    public void setListName(final Player player) {
        new Task(plugin.getPlugin()) {
            @Override
            public void executeAsync() {
                cache.add(plugin.getRanks().get(player.getUniqueId()));
                cache.add(plugin.getLevel().getFormattedLevel(player));
            }

            @Override
            public void executeSyncOnFinish() {
                if (!spectators.contains(player)) {
                    player.setPlayerListName(((Rank) cache.get(0)).getLongPrefix() + player.getName() + "§7 [" + cache.get(1) + "§7]");
                } else {
                    player.setPlayerListName("§7§o" + player.getName());
                }
            }
        };
    }

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

}