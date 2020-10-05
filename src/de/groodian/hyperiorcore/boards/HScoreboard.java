package de.groodian.hyperiorcore.boards;

import de.groodian.hyperiorcore.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HScoreboard implements Listener {

    private static final String OBJECTIVE_NAME = "SBbyGroodian";

    private Main plugin;
    private Map<Player, ScoreboardCache> cache;

    public HScoreboard(Main plugin) {
        this.plugin = plugin;
        this.cache = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent e) {
        removeFromCache(e.getPlayer());
    }

    public void registerScoreboard(Player player, String title, int lines) {

        cache.put(player, new ScoreboardCache(lines, title));

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null)
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null)
            objective.unregister();
        objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_NAME);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);

        for (int i = 0; i < lines; i++) {
            Team team = scoreboard.getTeam(i + "");
            if (team != null)
                team.unregister();
            team = scoreboard.registerNewTeam(i + "");
            team.addEntry(ChatColor.values()[i] + "");
            objective.getScore(ChatColor.values()[i] + "").setScore(lines - i - 1);
        }

        player.setScoreboard(scoreboard);
    }

    public void registerScoreboard(Player player, String title, int lines, int delay, int delayBetweenAnimation) {
        registerScoreboard(player, title, lines);
        startTitleAnimation(player, ChatColor.stripColor(title), delay, delayBetweenAnimation);
    }

    public void updateLine(int line, Player player, String lineContent) {

        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null)
            return;

        Team team = scoreboard.getTeam(line + "");
        if (team == null)
            return;

        if (cache.containsKey(player))
            if (cache.get(player).getLine(line) != null)
                if (cache.get(player).getLine(line).equals(lineContent))
                    return;

        cache.get(player).setLine(line, lineContent);

        // System.out.println("Update line " + line + " for " + player.getName());

        if (lineContent.length() > 16) {

            String prefix;
            String suffix;

            if (lineContent.charAt(15) == '§') {
                prefix = lineContent.substring(0, 15);
                suffix = "§" + lineContent.substring(16, lineContent.length());
            } else {
                prefix = lineContent.substring(0, 16);
                String color = ChatColor.getLastColors(prefix);
                if (color == "")
                    color = "§f";
                suffix = color + lineContent.substring(16, lineContent.length());
            }

            if (suffix.length() > 16)
                suffix = suffix.substring(0, 16);

            team.setPrefix(prefix);
            team.setSuffix(suffix);
        } else {
            team.setPrefix(lineContent);
            team.setSuffix("");
        }

    }

    public void updateTitle(Player player, String title) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null)
            return;

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null)
            return;

        if (cache.containsKey(player))
            if (cache.get(player).getTitle() != null)
                if (cache.get(player).getTitle().equals(title))
                    return;

        cache.get(player).setTitle(title);

        // System.out.println("Update title for " + player.getName());

        objective.setDisplayName(title);
    }

    private void startTitleAnimation(Player player, String title, int delay, int delayBetweenAnimation) {

        BukkitTask animationTask = new BukkitRunnable() {

            @Override
            public void run() {

                if (cache.containsKey(player)) {

                    int animationPos = cache.get(player).getAnimationPos();

                    if (animationPos <= title.length() + 2) {

                        String coloredTitle = "";

                        if (animationPos - 1 >= 0 && animationPos <= title.length())
                            coloredTitle += "§f§l" + title.substring(0, animationPos - 1) + "§e§l" + title.substring(animationPos - 1, animationPos);

                        if (animationPos + 1 <= title.length())
                            coloredTitle += "§6§l" + title.substring(animationPos, animationPos + 1) + "§f§l" + title.substring(animationPos + 1);

                        if (coloredTitle == "")
                            coloredTitle = "§f§l" + title;

                        updateTitle(player, coloredTitle);

                    } else {

                        if (animationPos >= title.length() + 2 + delayBetweenAnimation)
                            animationPos = -1;

                    }

                    animationPos++;
                    cache.get(player).setAnimationPos(animationPos);

                }

            }

            // wenn für alle gleichzeitig the animation gestartet wird so wie diese zu
            // unterschiedlichen Zeiten ausgeführt um so die Last zu verteilen
        }.runTaskTimer(plugin, new Random().nextInt(100), delay);

        if (cache.containsKey(player))
            cache.get(player).setAnimationTask(animationTask);

    }

    private void stopTitleAnimation(Player player) {
        if (cache.containsKey(player))
            if (cache.get(player).getAnimationTask() != null)
                cache.get(player).getAnimationTask().cancel();
    }

    public void unregisterScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null)
            return;

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null)
            return;

        removeFromCache(player);

        objective.unregister();

    }

    private void removeFromCache(Player player) {
        if (cache.containsKey(player)) {
            stopTitleAnimation(player);
            cache.remove(player);
        }
    }

    private class ScoreboardCache {

        private String[] lines;
        private String title;
        private BukkitTask animationTask;
        private int animationPos;

        public ScoreboardCache(int lines, String title) {
            this.lines = new String[lines];
            this.title = title;
            this.animationTask = null;
            this.animationPos = 0;
        }

        public String getLine(int line) {
            return lines[line];
        }

        public void setLine(int line, String lineContent) {
            this.lines[line] = lineContent;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BukkitTask getAnimationTask() {
            return animationTask;
        }

        public void setAnimationTask(BukkitTask animationTask) {
            this.animationTask = animationTask;
        }

        public int getAnimationPos() {
            return animationPos;
        }

        public void setAnimationPos(int animationPos) {
            this.animationPos = animationPos;
        }
    }

}
