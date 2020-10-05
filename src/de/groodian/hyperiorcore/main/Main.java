package de.groodian.hyperiorcore.main;

import de.groodian.hyperiorcore.boards.HScoreboard;
import de.groodian.hyperiorcore.boards.Prefix;
import de.groodian.hyperiorcore.coinsystem.CoinSystem;
import de.groodian.hyperiorcore.commands.RanksCommand;
import de.groodian.hyperiorcore.level.Level;
import de.groodian.hyperiorcore.listeners.MainListener;
import de.groodian.hyperiorcore.ranks.Ranks;
import de.groodian.hyperiorcore.util.MySQLManager;
import de.groodian.hyperiorcore.util.SpawnAble;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§7[§dHyperiorCore§7] §r";

    private static Main instance;

    private MySQLManager mySQLManager;

    private Ranks ranks;
    private Prefix prefix;
    private HScoreboard scoreboard;
    private CoinSystem coinSystem;
    private Level level;

    public void onEnable() {
        Mode.setModeType(ModeType.BUKKIT);

        Output.send(PREFIX + "§aDas Plugin wird geladen....");

        instance = this;

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MainListener(this), this);

        getCommand("hyperiorranks").setExecutor(new RanksCommand(this));

        mySQLManager = new MySQLManager();
        mySQLManager.connect();

        ranks = new Ranks(mySQLManager.getCoreMySQL());
        prefix = new Prefix(this);
        scoreboard = new HScoreboard(this);
        coinSystem = new CoinSystem(this);
        level = new Level(this);

        day();
        killAllMobs();
        updateSpawnAbles();

        Output.send(PREFIX + "§aGeladen!");
    }

    public void onDisable() {
        Output.send(PREFIX + "§cDas Plugin wird gestoppt....");

        mySQLManager.disconnect();

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
            List<Entity> entitys = w.getEntities();
            for (Entity entity : entitys) {
                entity.remove();
            }
        }
    }

    private void updateSpawnAbles() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (SpawnAble spawnAble : SpawnAble.spawnAbles) {
                    spawnAble.update();
                }
            }
        }.runTaskTimer(this, 40, 20);
    }

    public static Main getInstance() {
        return instance;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
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

    public CoinSystem getCoinSystem() {
        return coinSystem;
    }

    public Level getLevel() {
        return level;
    }

    public Plugin getPlugin() {
        return getPlugin(Main.class);
    }

}
