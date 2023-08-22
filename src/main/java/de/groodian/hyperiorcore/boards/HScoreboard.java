package de.groodian.hyperiorcore.boards;

import de.groodian.hyperiorcore.main.Main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class HScoreboard {

    private static final String OBJECTIVE_NAME = "SBbyGroodian";
    private static final Criteria CRITERIA = Criteria.create(OBJECTIVE_NAME);
    private static final List<String> NAMED_TEXT_COLORS = List.of("§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c",
            "§d", "§e", "§f");

    private final Main plugin;
    private final Map<Player, ScoreboardCache> cache;

    public HScoreboard(Main plugin) {
        this.plugin = plugin;
        cache = new HashMap<>();
    }

    public void registerScoreboard(Player player, Component title, int lines) {
        cache.put(player, new ScoreboardCache(lines, title.toString()));

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null)
            objective.unregister();
        objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, CRITERIA, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < lines; i++) {
            Team team = scoreboard.getTeam(String.valueOf(i));
            if (team != null)
                team.unregister();
            team = scoreboard.registerNewTeam(String.valueOf(i));
            team.addEntry(NAMED_TEXT_COLORS.get(i));
            objective.getScore(NAMED_TEXT_COLORS.get(i)).setScore(lines - i - 1);
        }

        player.setScoreboard(scoreboard);
    }

    public void registerScoreboard(Player player, Component title, int lines, int delay, int delayBetweenAnimation) {
        registerScoreboard(player, title, lines);
        startTitleAnimation(player, PlainTextComponentSerializer.plainText().serialize(title), delay, delayBetweenAnimation);
    }

    public void updateLine(int line, Player player, Component lineContent) {
        Scoreboard scoreboard = player.getScoreboard();

        Team team = scoreboard.getTeam(String.valueOf(line));
        if (team == null)
            return;

        ScoreboardCache scoreboardCache = cache.get(player);
        if (scoreboardCache == null)
            return;

        String cachedLine = scoreboardCache.getLine(line);
        String lineContentString = GsonComponentSerializer.gson().serialize(lineContent);
        if (cachedLine != null && cachedLine.equals(lineContentString))
            return;

        team.prefix(lineContent);

        cache.get(player).setLine(line, lineContentString);

        //System.out.println("Update line " + line + " for " + player.getName());
    }

    public void updateTitle(Player player, Component title) {
        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null)
            return;

        ScoreboardCache scoreboardCache = cache.get(player);
        if (scoreboardCache == null)
            return;

        String cachedTitle = scoreboardCache.getTitle();
        String titleString = GsonComponentSerializer.gson().serialize(title);
        if (cachedTitle != null && cachedTitle.equals(titleString))
            return;

        objective.displayName(title);

        cache.get(player).setTitle(titleString);

        // System.out.println("Update title for " + player.getName());
    }

    private void startTitleAnimation(Player player, String title, int delay, int delayBetweenAnimation) {

        BukkitTask animationTask = new BukkitRunnable() {

            @Override
            public void run() {

                if (cache.containsKey(player)) {

                    int animationPos = cache.get(player).getAnimationPos();

                    if (animationPos <= title.length() + 2) {

                        TextComponent.Builder coloredTitle = Component.text().decoration(TextDecoration.BOLD, true);

                        if (animationPos - 1 >= 0 && animationPos <= title.length()) {
                            coloredTitle.append(Component.text(title.substring(0, animationPos - 1), NamedTextColor.WHITE))
                                    .append(Component.text(title.substring(animationPos - 1, animationPos), NamedTextColor.YELLOW));

                        } else if (animationPos + 1 <= title.length()) {
                            coloredTitle.append(Component.text(title.substring(animationPos, animationPos + 1), NamedTextColor.YELLOW))
                                    .append(Component.text(title.substring(animationPos + 1), NamedTextColor.WHITE));


                        } else {
                            coloredTitle.append(Component.text(title, NamedTextColor.YELLOW));
                        }

                        updateTitle(player, coloredTitle.build());

                    } else {
                        if (animationPos >= title.length() + 2 + delayBetweenAnimation)
                            animationPos = -1;
                    }

                    animationPos++;
                    cache.get(player).setAnimationPos(animationPos);

                }

            }

            // wenn für alle gleichzeitig the animation gestartet wird so wie diese zu
            // unterschiedlichen Zeiten ausgeführt, um so die Last zu verteilen
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
        stopTitleAnimation(player);

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null)
            return;

        removeFromCache(player);

        objective.unregister();
    }

    public void removeFromCache(Player player) {
        if (cache.containsKey(player)) {
            stopTitleAnimation(player);
            cache.remove(player);
        }
    }

    private class ScoreboardCache {

        private final String[] lines;
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
