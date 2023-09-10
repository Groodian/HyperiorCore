package de.groodian.hyperiorcore.commands;

import de.groodian.hyperiorcore.command.HArgument;
import de.groodian.hyperiorcore.command.HCommandPaper;
import de.groodian.hyperiorcore.command.HTabCompleteType;
import de.groodian.hyperiorcore.guis.StatsGUI;
import de.groodian.hyperiorcore.main.Main;
import de.groodian.hyperiorcore.user.MinecraftPartyStats;
import de.groodian.hyperiorcore.user.User;
import de.groodian.hyperiorcore.util.Task;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class StatsCommandPaper extends HCommandPaper<Player> {

    private static final Component PREFIX = Component.text("[", NamedTextColor.GRAY)
            .append(Component.text("Stats", NamedTextColor.YELLOW))
            .append(Component.text("] ", NamedTextColor.GRAY));

    private final Main plugin;

    public StatsCommandPaper(Main plugin) {
        super(Player.class, "stats", "Show the stats of a user", PREFIX, null, List.of(),
                List.of(new HArgument("player", false, HTabCompleteType.PLAYER, true)));
        this.plugin = plugin;
    }

    @Override
    protected void onCall(Player player, String[] args) {
        new Task(plugin) {
            @Override
            public void executeAsync() {
                User user;

                if (args.length == 0) {
                    user = plugin.getUserManager().get(player.getUniqueId());
                } else {
                    user = plugin.getUserManager().loadUser(args[0]);
                }

                if (user == null) {
                    sendMsg(player, Component.text("This player has never played.", NamedTextColor.RED));
                    return;
                }

                MinecraftPartyStats.Player stats = MinecraftPartyStats.loadPlayer(plugin.getDatabaseManager(), user.getUuid());

                cache.add(user);
                cache.add(stats);
            }

            @Override
            public void executeSyncOnFinish() {
                if (cache.size() != 2) {
                    return;
                }

                plugin.getDefaultGUIManager()
                        .open(player, new StatsGUI((User) cache.get(0), (MinecraftPartyStats.Player) cache.get(1)));
            }
        };

    }

}
