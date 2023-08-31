package de.groodian.hyperiorcore.boards;

import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.user.Rank;
import de.groodian.hyperiorcore.user.User;
import de.groodian.hyperiorcore.user.XP;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Prefix {

    private final Main plugin;

    private final List<Player> spectators;

    public Prefix(Main plugin) {
        this.plugin = plugin;
        this.spectators = new ArrayList<>();
    }

    public void setPrefix(Player player) {

        // Hohle von jedem Spieler das Scoreboard und adde den neuen Spieler, wenn ein
        // Spieler "neu" ist, werden für diesen Spieler alle Spieler, die
        // bereits auf dem Server sind zu seinem Scoreboard hinzugefügt
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
        User user = plugin.getUserManager().get(player.getUniqueId());
        if (user == null) {
            return;
        }
        Rank rank = user.getRank();

        String teamName = rank.value() + "-" + rank.name();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(rank.shortPrefix());
        }

        Team spectator = scoreboard.getTeam("999-Spectator");
        if (spectator == null) {
            spectator = scoreboard.registerNewTeam("999-Spectator");
            spectator.prefix(Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true).build());
            spectator.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            spectator.setCanSeeFriendlyInvisibles(true);
        }

        if (!spectators.contains(player)) {
            player.displayName(Component.text(player.getName(), rank.color()));
            team.addEntry(player.getName());
        } else {
            player.displayName(Component.text(player.getName(), NamedTextColor.GRAY));
            spectator.addEntry(player.getName());
        }
    }

    public void setListName(final Player player) {
        User user = plugin.getUserManager().get(player.getUniqueId());
        if (user == null) {
            return;
        }

        if (!spectators.contains(player)) {
            player.playerListName(user.getRank()
                    .longPrefix()
                    .append(Component.text(player.getName()))
                    .append(Component.text(" [", NamedTextColor.GRAY))
                    .append(XP.getFormattedLevel(XP.getLevel(user.getTotalXP())))
                    .append(Component.text("]", NamedTextColor.GRAY)));
        } else {
            player.playerListName(Component.text(player.getName(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true));
        }
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