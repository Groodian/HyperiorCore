package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.boards.HScoreboard;
import de.groodian.hyperiorcore.boards.Prefix;
import de.groodian.hyperiorcore.command.HCommandManagerPaper;
import de.groodian.hyperiorcore.commands.HelpCommandPaper;
import de.groodian.hyperiorcore.commands.RanksCommandPaper;
import de.groodian.hyperiorcore.commands.StatsCommandPaper;
import de.groodian.hyperiorcore.gui.GUI;
import de.groodian.hyperiorcore.gui.GUIManager;
import de.groodian.hyperiorcore.listeners.MainListener;
import de.groodian.hyperiorcore.spawnable.SpawnAble;
import de.groodian.hyperiorcore.spawnable.SpawnAbleManager;
import de.groodian.hyperiorcore.user.Ranks;
import de.groodian.hyperiorcore.user.UserManager;
import de.groodian.hyperiorcore.util.DatabaseManagerPaper;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§7[§dHyperiorCore§7] §r";

    private static Main instance;

    private DatabaseManagerPaper databaseManager;
    private UserManager userManager;
    private SpawnAbleManager spawnAbleManager;
    private HCommandManagerPaper hCommandManagerPaper;
    private Ranks ranks;
    private Prefix prefix;
    private HScoreboard scoreboard;
    private GUIManager defaultGUIManager;

    public void onEnable() {
        Mode.setModeType(ModeType.PAPER);

        Output.send(PREFIX + "§aDas Plugin wird geladen...");

        instance = this;

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MainListener(this), this);

        databaseManager = new DatabaseManagerPaper(HyperiorCore.DB_ADDRESS, HyperiorCore.DB_PORT, HyperiorCore.DB_DATABASE,
                HyperiorCore.DB_USER, HyperiorCore.DB_PASSWORD);
        userManager = new UserManager(databaseManager);
        spawnAbleManager = new SpawnAbleManager();
        hCommandManagerPaper = new HCommandManagerPaper(this);
        ranks = new Ranks(databaseManager, userManager);
        prefix = new Prefix(this);
        scoreboard = new HScoreboard(this);
        defaultGUIManager = new GUIManager(GUI.class, this);

        hCommandManagerPaper.registerCommand(this, new RanksCommandPaper(this));
        hCommandManagerPaper.registerCommand(this, new HelpCommandPaper(this));
        hCommandManagerPaper.registerCommand(this, new StatsCommandPaper(this));

        day();
        killAllMobs();
        updateSpawnAbles();

        Output.send(PREFIX + "§aGeladen!");
    }

    public void onDisable() {
        Output.send(PREFIX + "§cDas Plugin wird gestoppt...");

        databaseManager.disconnect();

        Output.send(PREFIX + "§cGestoppt!");
    }

    private void day() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World w : Bukkit.getWorlds()) {
                    w.setTime(2000);
                    w.setWeatherDuration(1200);
                    w.setThunderDuration(1200);
                    w.setStorm(false);
                    w.setThundering(false);
                }
            }
        }.runTaskTimer(this, 0, 1000);
    }

    private void killAllMobs() {
        for (World w : Bukkit.getWorlds()) {
            List<Entity> entities = w.getEntities();
            for (Entity entity : entities) {
                entity.remove();
            }
        }
    }

    private void updateSpawnAbles() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (SpawnAble spawnAble : spawnAbleManager.getSpawnAbleList()) {
                    spawnAble.update();
                }
            }
        }.runTaskTimer(this, 40, 20);
    }

    public static Main getInstance() {
        return instance;
    }

    public DatabaseManagerPaper getDatabaseManager() {
        return databaseManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public SpawnAbleManager getSpawnAbleManager() {
        return spawnAbleManager;
    }

    public HCommandManagerPaper getHCommandManagerPaper() {
        return hCommandManagerPaper;
    }

    public Ranks getRanks() {
        return ranks;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public HScoreboard getScoreboard() {
        return scoreboard;
    }

    public GUIManager getDefaultGUIManager() {
        return defaultGUIManager;
    }

    public Plugin getPlugin() {
        return getPlugin(Main.class);
    }

}
